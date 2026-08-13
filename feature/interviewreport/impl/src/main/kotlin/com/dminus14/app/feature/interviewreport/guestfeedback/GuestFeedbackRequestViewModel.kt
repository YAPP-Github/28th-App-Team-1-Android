package com.dminus14.app.feature.interviewreport.guestfeedback

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.EmptyAttitudeAxesException
import com.dminus14.app.domain.exception.FeedbackShareAlreadyExistsException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidAttitudeAxisException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.TooManyAttitudeAxesException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.usecase.CreateFeedbackShareDynamicLinkUseCase
import com.dminus14.app.domain.usecase.CreateFeedbackShareUseCase
import com.dminus14.app.domain.usecase.EndFeedbackShareUseCase
import com.dminus14.app.domain.usecase.GetSavedFeedbackShareTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val COPIED_NOTICE_DURATION_MS = 2000L

@HiltViewModel
@Suppress("TooManyFunctions")
class GuestFeedbackRequestViewModel
    @Inject
    constructor(
        private val createFeedbackShare: CreateFeedbackShareUseCase,
        private val endFeedbackShare: EndFeedbackShareUseCase,
        private val getSavedFeedbackShareToken: GetSavedFeedbackShareTokenUseCase,
        private val createFeedbackShareDynamicLink: CreateFeedbackShareDynamicLinkUseCase,
    ) : MviViewModel<
            GuestFeedbackRequestIntent,
            GuestFeedbackRequestState,
            GuestFeedbackRequestEffect,
        >(
            GuestFeedbackRequestState(),
        ) {
        private var sessionId: Long = 0L

        fun bindSessionId(sessionId: Long) {
            if (this.sessionId == 0L) this.sessionId = sessionId
        }

        override fun onIntent(intent: GuestFeedbackRequestIntent) {
            when (intent) {
                GuestFeedbackRequestIntent.Load -> {
                    load()
                }

                is GuestFeedbackRequestIntent.ToggleAxis -> {
                    toggleAxis(intent.axis)
                }

                GuestFeedbackRequestIntent.ClickClose -> {
                    sendEffect(GuestFeedbackRequestEffect.NavigateBack)
                }

                GuestFeedbackRequestIntent.ClickSubmit -> {
                    onClickSubmit()
                }

                GuestFeedbackRequestIntent.ClickCopyLink -> {
                    copyLink()
                }

                GuestFeedbackRequestIntent.DismissShareLinkModal -> {
                    reduce { copy(shareLink = null) }
                }
            }
        }

        /** sessionId 에 저장된 공유 링크 token 이 있으면 하단 버튼을 "피드백 종료하기"로 바꾼다. */
        private fun load() {
            if (sessionId <= 0L) return
            viewModelScope.launch {
                val savedToken = getSavedFeedbackShareToken(sessionId)
                reduce { copy(hasActiveShare = savedToken != null) }
            }
        }

        private fun toggleAxis(axis: GuestFeedbackAxisCode) {
            reduce {
                val next =
                    if (axis in selectedAxes) {
                        // 최소 1개는 유지한다(서버 계약 1~5개).
                        if (selectedAxes.size <= 1) selectedAxes else selectedAxes - axis
                    } else {
                        selectedAxes + axis
                    }
                copy(selectedAxes = next)
            }
        }

        private fun onClickSubmit() {
            if (state.value.hasActiveShare) {
                endShare()
            } else {
                createShare()
            }
        }

        private fun createShare() {
            val current = state.value
            if (current.submitting || current.selectedAxes.isEmpty()) return
            reduce { copy(submitting = true) }
            viewModelScope.launch {
                createFeedbackShare(sessionId, current.selectedAxes.toList())
                    .onSuccess { token ->
                        // 아래는 항상 성공한다(동적 링크 생성 실패 시 원시 딥링크로 self-heal).
                        val shareLink = createFeedbackShareDynamicLink(token).getOrThrow()
                        reduce {
                            copy(
                                submitting = false,
                                shareLink = shareLink,
                                hasActiveShare = true,
                            )
                        }
                    }.onFailure { error ->
                        handleCreateShareFailure(error)
                    }
            }
        }

        /**
         * `POST .../share`(create_1) 전용 비즈니스 예외는 인라인으로 안내하고, 나머지
         * network/server/unknown 은 공통 처리로 위임한다.
         */
        private suspend fun handleCreateShareFailure(error: Throwable) {
            when (error) {
                is EmptyAttitudeAxesException,
                is TooManyAttitudeAxesException,
                is InvalidAttitudeAxisException,
                -> {
                    reduce { copy(submitting = false) }
                    sendEffect(GuestFeedbackRequestEffect.ShowToast(error.message))
                }

                is FeedbackShareAlreadyExistsException -> {
                    handleAlreadyExistsFailure(error)
                }

                is InterviewSessionNotFoundException -> {
                    reduce { copy(submitting = false) }
                    sendEffect(GuestFeedbackRequestEffect.ShowToast(error.message))
                    sendEffect(GuestFeedbackRequestEffect.NavigateBack)
                }

                else -> {
                    handleCommonError(error)
                }
            }
        }

        /**
         * 서버에는 이미 활성 공유 링크가 있는 상태(생성 API 는 충돌 시 기존 token 을 돌려주지
         * 않음). 이 기기에 그 링크를 만들 때 저장해둔 실제 token 이 남아있다면 그걸로 딥링크를
         * 다시 만들어 방금 생성에 성공한 것과 똑같이 보여준다. 로컬에 token 이 전혀 없으면(다른
         * 기기에서 만들었거나 앱 데이터가 지워진 경우) 재구성할 수 없으므로 기존처럼 종료 가능
         * 상태로만 맞추고 안내한다.
         */
        private suspend fun handleAlreadyExistsFailure(error: FeedbackShareAlreadyExistsException) {
            val savedToken = getSavedFeedbackShareToken(sessionId)
            if (savedToken != null) {
                // 아래는 항상 성공한다(동적 링크 생성 실패 시 원시 딥링크로 self-heal).
                val shareLink = createFeedbackShareDynamicLink(savedToken).getOrThrow()
                reduce {
                    copy(
                        submitting = false,
                        shareLink = shareLink,
                        hasActiveShare = true,
                    )
                }
            } else {
                reduce { copy(submitting = false, hasActiveShare = true) }
                sendEffect(GuestFeedbackRequestEffect.ShowToast(error.message))
            }
        }

        private fun endShare() {
            if (state.value.submitting) return
            reduce { copy(submitting = true) }
            viewModelScope.launch {
                endFeedbackShare(sessionId)
                    .onSuccess {
                        reduce { copy(submitting = false, hasActiveShare = false) }
                        sendEffect(GuestFeedbackRequestEffect.ShowToast("피드백 요청을 종료했어요."))
                        sendEffect(GuestFeedbackRequestEffect.NavigateBack)
                    }.onFailure { error ->
                        handleCommonError(error)
                    }
            }
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleCommonError(error: Throwable) {
            reduce { copy(submitting = false) }
            when {
                error is NetworkUnavailableException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                }

                error is ServerException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                }

                else -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
                }
            }
        }

        private fun copyLink() {
            val link = state.value.shareLink ?: return
            sendEffect(GuestFeedbackRequestEffect.CopyToClipboard(link))
            reduce { copy(linkCopied = true) }
            viewModelScope.launch {
                delay(COPIED_NOTICE_DURATION_MS)
                sendEffect(GuestFeedbackRequestEffect.NavigateBack)
            }
        }
    }

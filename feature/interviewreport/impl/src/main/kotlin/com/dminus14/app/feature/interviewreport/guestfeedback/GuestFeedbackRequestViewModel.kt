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
import com.dminus14.app.domain.usecase.CreateFeedbackShareUseCase
import com.dminus14.app.domain.usecase.EndFeedbackShareUseCase
import com.dminus14.app.domain.usecase.GetSavedFeedbackShareTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val COPIED_NOTICE_DURATION_MS = 2000L

/** 지인 피드백 딥링크 조립 베이스. 실제 스킴/호스트는 앱 딥링크 규칙 확정 시 교체한다. */
private const val FEEDBACK_SHARE_LINK_BASE = "https://hilit.app/f/"

@HiltViewModel
class GuestFeedbackRequestViewModel
    @Inject
    constructor(
        private val createFeedbackShare: CreateFeedbackShareUseCase,
        private val endFeedbackShare: EndFeedbackShareUseCase,
        private val getSavedFeedbackShareToken: GetSavedFeedbackShareTokenUseCase,
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
                        reduce {
                            copy(
                                submitting = false,
                                shareLink = FEEDBACK_SHARE_LINK_BASE + token,
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
                    // 서버에는 이미 활성 링크가 있는 상태. 종료 가능 상태로 맞춰 다시 시도를 막는다.
                    reduce { copy(submitting = false, hasActiveShare = true) }
                    sendEffect(GuestFeedbackRequestEffect.ShowToast(error.message))
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

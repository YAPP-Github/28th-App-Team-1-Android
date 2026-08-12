package com.dminus14.app.feature.interviewreport.guestfeedback

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.usecase.CreateFeedbackShareUseCase
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
                is GuestFeedbackRequestIntent.ToggleAxis -> {
                    toggleAxis(intent.axis)
                }

                GuestFeedbackRequestIntent.ClickClose -> {
                    sendEffect(GuestFeedbackRequestEffect.NavigateBack)
                }

                GuestFeedbackRequestIntent.ClickSubmit -> {
                    submit()
                }

                GuestFeedbackRequestIntent.ClickCopyLink -> {
                    copyLink()
                }
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

        private fun submit() {
            val current = state.value
            if (current.submitting || current.selectedAxes.isEmpty()) return
            reduce { copy(submitting = true) }
            viewModelScope.launch {
                createFeedbackShare(sessionId, current.selectedAxes.toList())
                    .onSuccess { token ->
                        reduce {
                            copy(submitting = false, shareLink = FEEDBACK_SHARE_LINK_BASE + token)
                        }
                    }.onFailure {
                        reduce { copy(submitting = false) }
                        sendEffect(
                            GuestFeedbackRequestEffect.ShowToast("링크 생성에 실패했어요. 다시 시도해 주세요."),
                        )
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

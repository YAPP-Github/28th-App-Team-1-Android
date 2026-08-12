package com.dminus14.app.feature.interviewreport.guestfeedback

import androidx.compose.runtime.Immutable
import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.domain.model.GuestFeedbackAxisCode

sealed interface GuestFeedbackRequestIntent : MviIntent {
    data class ToggleAxis(
        val axis: GuestFeedbackAxisCode,
    ) : GuestFeedbackRequestIntent

    data object ClickClose : GuestFeedbackRequestIntent

    data object ClickSubmit : GuestFeedbackRequestIntent

    data object ClickCopyLink : GuestFeedbackRequestIntent
}

@Immutable
data class GuestFeedbackRequestState(
    val selectedAxes: Set<GuestFeedbackAxisCode> = GuestFeedbackAxisCode.entries.toSet(),
    val submitting: Boolean = false,
    /** 링크 생성 성공 시의 공유 링크. non-null 이면 "링크 생성 완료" 모달을 노출한다. */
    val shareLink: String? = null,
    /** 링크 복사 완료 후의 2초 안내 모달 노출 여부. */
    val linkCopied: Boolean = false,
) : MviState

sealed interface GuestFeedbackRequestEffect : MviEffect {
    data object NavigateBack : GuestFeedbackRequestEffect

    data class CopyToClipboard(
        val link: String,
    ) : GuestFeedbackRequestEffect

    data class ShowToast(
        val message: String,
    ) : GuestFeedbackRequestEffect
}

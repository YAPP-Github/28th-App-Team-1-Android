package com.dminus14.app.feature.interviewreport

import androidx.compose.runtime.Immutable
import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface InterviewReportIntent : MviIntent {
    data object Load : InterviewReportIntent

    data object ClickClose : InterviewReportIntent

    data object ClickRetry : InterviewReportIntent

    data object ClickWatchVideo : InterviewReportIntent

    data class ClickWatchScene(
        val startSec: Float,
    ) : InterviewReportIntent

    data class ClickHighlight(
        val cardIndex: Int,
        val spanIndex: Int,
    ) : InterviewReportIntent

    data object DismissHighlight : InterviewReportIntent

    data object ClickGuestFeedbackInvite : InterviewReportIntent
}

@Immutable
data class InterviewReportState(
    val phase: Phase = Phase.Loading,
    val sessionId: Long = 0L,
    val selectedHighlight: SelectedHighlight? = null,
) : MviState {
    sealed interface Phase {
        data object Loading : Phase

        data object Failed : Phase

        data object InsufficientAnalysis : Phase

        /** 리포트가 준비된 뒤 UI 렌더에 사용하는 최종 모델. C3에서 채워진다. */
        data class Ready(
            val placeholder: Unit = Unit,
        ) : Phase
    }

    @Immutable
    data class SelectedHighlight(
        val cardIndex: Int,
        val spanIndex: Int,
    )
}

sealed interface InterviewReportEffect : MviEffect {
    data object NavigateBack : InterviewReportEffect

    data class NavigateToPlayer(
        val sessionId: Long,
        val startSec: Float?,
    ) : InterviewReportEffect

    data class NavigateToGuestFeedback(
        val sessionId: Long,
    ) : InterviewReportEffect

    data class ShowToast(
        val message: String,
    ) : InterviewReportEffect
}

package com.dminus14.app.feature.interview.error

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.feature.interview.api.InterviewErrorType

sealed interface InterviewErrorIntent : MviIntent {
    data class Load(
        val errorType: InterviewErrorType,
    ) : InterviewErrorIntent

    data object ClickAbort : InterviewErrorIntent

    data object ClickResume : InterviewErrorIntent
}

data class InterviewErrorState(
    val errorType: InterviewErrorType = InterviewErrorType.MIC_DEVICE,
    val isLoading: Boolean = false,
    val canResume: Boolean = true,
    val canRetryAnswerSubmission: Boolean = false,
    val failureMessage: String? = null,
) : MviState

sealed interface InterviewErrorEffect : MviEffect {
    data object InterviewAbandonCompleted : InterviewErrorEffect

    data object InterviewResumeConfirmed : InterviewErrorEffect

    data object SttFailureAcknowledged : InterviewErrorEffect

    data object AnswerSubmissionRecovered : InterviewErrorEffect
}

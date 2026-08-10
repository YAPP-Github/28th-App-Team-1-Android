package com.dminus14.app.feature.interview.error

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.feature.interview.api.InterviewErrorType

/** 면접 오류 MVI Intent */
sealed interface InterviewErrorIntent : MviIntent {
    data object ClickAbort : InterviewErrorIntent

    data object ClickResume : InterviewErrorIntent
}

/** 면접 오류 MVI State */
data class InterviewErrorState(
    val errorType: InterviewErrorType = InterviewErrorType.MIC_DEVICE,
) : MviState

/** 면접 오류 MVI Effect */
sealed interface InterviewErrorEffect : MviEffect {
    data object NavigateToHome : InterviewErrorEffect

    data object ResumeInterview : InterviewErrorEffect
}

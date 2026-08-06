package com.dminus14.app.feature.interview.interview

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.feature.interview.InterviewConstants

/** 현재 발언자 */
enum class InterviewSpeaker {
    AI,
    User,
    ;

    operator fun not(): InterviewSpeaker =
        when (this) {
            InterviewSpeaker.AI -> InterviewSpeaker.AI
            InterviewSpeaker.User -> InterviewSpeaker.User
        }
}

/** 면접 진행 단계 */
enum class InterviewScreenState {
    PREPARING,
    ALMOST_PREPARED,
    PREPARED,
    ONGOING,
    FINISHED,
}

/** 면접 MVI Intent */
sealed interface InterviewIntent : MviIntent {
    data object StartInterview : InterviewIntent

    data object ChangeSpeaker : InterviewIntent
}

/** 면접 MVI State */
data class InterviewState(
    var interviewScreenState: InterviewScreenState = InterviewScreenState.PREPARING,
    var isInterviewReady: Boolean = false,
    var speaker: InterviewSpeaker = InterviewSpeaker.AI,
    val elapsedSeconds: Int = 0,
) : MviState {
    val remainingSeconds: Int
        get() = InterviewConstants.MAX_INTERVIEW_SECONDS - elapsedSeconds

    val canFinishedEarly: Boolean
        get() = InterviewConstants.CAN_FINISH_INTERVIEW_SECONDS - elapsedSeconds < 0

    val isInterviewOngoing: Boolean
        get() = isInterviewReady && remainingSeconds > 0
}

/** 면접 MVI Effect */
sealed interface InterviewEffect : MviEffect

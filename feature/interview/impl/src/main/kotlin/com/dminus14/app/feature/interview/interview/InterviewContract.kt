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
            InterviewSpeaker.AI -> InterviewSpeaker.User
            InterviewSpeaker.User -> InterviewSpeaker.AI
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
    data object CheckCameraPermission : InterviewIntent

    data object StartInterview : InterviewIntent

    data object ChangeSpeaker : InterviewIntent

    data object ReportCameraBindingFailure : InterviewIntent

    data object ClickPermissionDeniedBack : InterviewIntent

    data object ClickFinishInterview : InterviewIntent
}

/** 면접 MVI State */
data class InterviewState(
    var interviewScreenState: InterviewScreenState = InterviewScreenState.PREPARING,
    var isInterviewReady: Boolean = false,
    var speaker: InterviewSpeaker = InterviewSpeaker.AI,
    // 후속 구현: PermissionManager에서 CAMERA 권한의 실제 상태를 조회해 갱신한다.
    val isCameraPermissionGranted: Boolean = false,
    val elapsedSeconds: Int = 0,
) : MviState {
    val remainingSeconds: Int
        get() = InterviewConstants.MAX_INTERVIEW_SECONDS - elapsedSeconds

    val canFinishedEarly: Boolean
        get() = elapsedSeconds >= InterviewConstants.CAN_FINISH_INTERVIEW_SECONDS

    val isInterviewOngoing: Boolean
        get() = isInterviewReady && remainingSeconds > 0
}

/** 면접 MVI Effect */
sealed interface InterviewEffect : MviEffect {
    data object CameraBindingFailed : InterviewEffect

    data object PermissionDeniedExitRequested : InterviewEffect

    data object FinishRequested : InterviewEffect
}

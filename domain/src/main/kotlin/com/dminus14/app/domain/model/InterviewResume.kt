package com.dminus14.app.domain.model

/**
 * 면접 세션 재개 상태 정보이다.
 */
data class InterviewResumeStatus(
    val resumeState: String,
    val startedAt: String?,
    val elapsedSeconds: Int?,
    val status: String?,
)

/**
 * 면접 세션 재개 확정 결과이다.
 */
data class InterviewResumeConfirm(
    val nextQuestion: NextQuestion?,
    val sessionEnded: Boolean,
    val wrapUpMessage: WrapUpMessage?,
    val endType: String?,
    val status: String?,
    val abandonCause: String?,
    val endedAt: String?,
)

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
 * 면접 세션 재개 가능 상태.
 *
 * @property RESUMABLE 이어서 진행 가능한 세션.
 * @property ENDED 종료되어 이어서 진행할 수 없는 세션.
 */
enum class InterviewSessionResumeState {
    RESUMABLE,
    ENDED,
    ;

    companion object {
        /** 서버 raw 문자열을 enum으로 변환한다. 정의되지 않은 값이면 null. */
        fun fromRaw(rawState: String): InterviewSessionResumeState? =
            entries.firstOrNull { state -> state.name.equals(rawState, ignoreCase = true) }
    }
}

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

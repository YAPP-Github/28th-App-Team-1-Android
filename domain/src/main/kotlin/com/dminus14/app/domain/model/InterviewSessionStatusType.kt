package com.dminus14.app.domain.model

/**
 * 면접 세션 준비 상태.
 *
 * 알 수 없는 값은 [UNKNOWN]으로 흡수한다.
 */
enum class InterviewSessionStatusType {
    PROCESSING,
    READY,
    FAILED,
    UNKNOWN,
    ;

    companion object {
        fun fromRaw(rawStatus: String): InterviewSessionStatusType =
            entries.firstOrNull { status -> status.name == rawStatus } ?: UNKNOWN
    }
}

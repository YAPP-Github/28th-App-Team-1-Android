package com.dminus14.app.domain.model

/** 면접 세션 준비 상태. 알 수 없는 서버 값은 원문을 보존한다. */
sealed interface InterviewSessionStatusType {
    data object PROCESSING : InterviewSessionStatusType

    data object READY : InterviewSessionStatusType

    data object FAILED : InterviewSessionStatusType

    data class Unknown(
        val rawValue: String,
    ) : InterviewSessionStatusType

    companion object {
        fun fromRaw(rawStatus: String): InterviewSessionStatusType =
            when (rawStatus) {
                "PROCESSING" -> PROCESSING
                "READY" -> READY
                "FAILED" -> FAILED
                else -> Unknown(rawStatus)
            }
    }
}

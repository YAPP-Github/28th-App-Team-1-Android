package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewResumeStatus
import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/interview/sessions/{sessionId}/resume
 */
data class InterviewResumeStatusRequestDto(
    @SerializedName("dummy")
    val dummy: String? = null,
)

data class InterviewResumeStatusResponseDto(
    @SerializedName("resumeState")
    val resumeState: String,
    @SerializedName("startedAt")
    val startedAt: String?,
    @SerializedName("elapsedSeconds")
    val elapsedSeconds: Int?,
    @SerializedName("status")
    val status: String?,
) {
    fun toDomain(): InterviewResumeStatus =
        InterviewResumeStatus(
            resumeState = resumeState,
            startedAt = startedAt,
            elapsedSeconds = elapsedSeconds,
            status = status,
        )
}

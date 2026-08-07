package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewAbandon
import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/interview/sessions/{sessionId}/abandon
 */
data class InterviewAbandonRequestDto(
    @SerializedName("cause")
    val cause: String? = null,
)

data class InterviewAbandonResponseDto(
    @SerializedName("sessionId")
    val sessionId: Long,
    @SerializedName("status")
    val status: String,
    @SerializedName("abandonCause")
    val abandonCause: String,
    @SerializedName("endedAt")
    val endedAt: String,
    @SerializedName("ticketOutcome")
    val ticketOutcome: String,
    @SerializedName("reportGenerating")
    val reportGenerating: Boolean,
) {
    fun toDomain(): InterviewAbandon =
        InterviewAbandon(
            sessionId = sessionId,
            status = status,
            abandonCause = abandonCause,
            endedAt = endedAt,
            ticketOutcome = ticketOutcome,
            reportGenerating = reportGenerating,
        )
}

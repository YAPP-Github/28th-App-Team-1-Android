package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.model.InterviewTicketOutcome
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
            status = status.toInterviewTerminalStatus(),
            abandonCause = abandonCause.toInterviewAbandonCause(),
            endedAt = endedAt,
            ticketOutcome = ticketOutcome.toInterviewTicketOutcome(),
            reportGenerating = reportGenerating,
        )
}

private fun String.toInterviewTicketOutcome(): InterviewTicketOutcome =
    when (this) {
        "COMMITTED" -> InterviewTicketOutcome.Committed
        "RELEASED" -> InterviewTicketOutcome.Released
        else -> InterviewTicketOutcome.Unknown(this)
    }

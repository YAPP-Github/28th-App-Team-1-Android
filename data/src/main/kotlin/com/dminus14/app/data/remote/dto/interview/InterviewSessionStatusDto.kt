package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.dminus14.app.domain.model.SummaryQuestion
import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/interview/sessions/{sessionId}/status
 */
data class InterviewSessionStatusRequestDto(
    @SerializedName("dummy")
    val dummy: String? = null,
)

data class InterviewSessionStatusResponseDto(
    @SerializedName("sessionId")
    val sessionId: Long? = null,
    @SerializedName("status")
    val status: String,
    @SerializedName("startedAt")
    val startedAt: String? = null,
    @SerializedName("ticketOutcome")
    val ticketOutcome: String? = null,
    @SerializedName("reportGenerating")
    val reportGenerating: Boolean? = null,
    @SerializedName("summaryQuestion")
    val summaryQuestion: SummaryQuestionResponseDto? = null,
) {
    fun toDomain(): InterviewSessionStatus =
        InterviewSessionStatus(
            status = InterviewSessionStatusType.fromRaw(status),
            startedAt = startedAt,
            summaryQuestion = summaryQuestion?.toDomain(),
        )
}

data class SummaryQuestionResponseDto(
    @SerializedName("questionId")
    val questionId: Long,
    @SerializedName("ttsAudio")
    val ttsAudio: String?,
) {
    fun toDomain(): SummaryQuestion =
        SummaryQuestion(
            questionId = questionId,
            ttsAudio = ttsAudio,
        )
}

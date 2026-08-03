package com.dminus14.app.data.remote.dto

import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.dminus14.app.domain.model.SummaryQuestion
import com.google.gson.annotations.SerializedName

data class CreateInterviewSessionRequestDto(
    @SerializedName("portfolioId")
    val portfolioId: String,
    @SerializedName("jobRole")
    val jobRole: String,
    @SerializedName("careerYears")
    val careerYears: Int,
    @SerializedName("jdUrl")
    val jdUrl: String? = null,
    @SerializedName("jdText")
    val jdText: String? = null,
    @SerializedName("freeText")
    val freeText: String? = null,
) {
    companion object {
        fun from(request: InterviewSessionRequest): CreateInterviewSessionRequestDto =
            CreateInterviewSessionRequestDto(
                portfolioId = request.portfolioId,
                jobRole = request.jobRole,
                careerYears = request.careerYears,
                jdUrl = request.jdUrl,
                jdText = request.jdText,
                freeText = request.freeText,
            )
    }
}

data class InterviewSessionResponseDto(
    @SerializedName("sessionId")
    val sessionId: Long,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusUrl")
    val statusUrl: String,
) {
    fun toDomain(): InterviewSessionResult =
        InterviewSessionResult(
            sessionId = sessionId,
            status = InterviewSessionStatusType.fromRaw(status),
            statusUrl = statusUrl,
        )
}

data class InterviewSessionStatusResponseDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("startedAt")
    val startedAt: String?,
    @SerializedName("summaryQuestion")
    val summaryQuestion: SummaryQuestionResponseDto?,
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

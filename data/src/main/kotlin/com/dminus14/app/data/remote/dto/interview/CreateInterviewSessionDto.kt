package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/interview/sessions
 */
data class CreateInterviewSessionRequestDto(
    @SerializedName("portfolioId")
    val portfolioId: String? = null,
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
    val statusUrl: String? = null,
) {
    fun toDomain(): InterviewSessionResult =
        InterviewSessionResult(
            sessionId = sessionId,
            status = InterviewSessionStatusType.fromRaw(status),
            statusUrl = statusUrl.orEmpty(),
        )
}

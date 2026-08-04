package com.dminus14.app.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/interview/sessions/{sessionId}/video/complete
 */
data class InterviewVideoCompleteRequestDto(
    @SerializedName("wrapUpStartSec")
    val wrapUpStartSec: Float? = null,
    @SerializedName("wrapUpEndSec")
    val wrapUpEndSec: Float? = null,
    @SerializedName("videoFileName")
    val videoFileName: String? = null,
    @SerializedName("s3Key")
    val s3Key: String? = null,
)

/**
 * POST api/v1/interview/sessions/{sessionId}/video/complete 응답용 더미 DTO.
 * 실제 API에서는 Unit (ApiResponseDto<Unit>)을 반환한다.
 */
data class InterviewVideoCompleteResponseDto(
    @SerializedName("dummy")
    val dummy: String? = null,
)

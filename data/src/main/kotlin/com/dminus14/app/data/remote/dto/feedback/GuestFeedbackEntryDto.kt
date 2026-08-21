@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.feedback

import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/feedback/guest/{token}
 *
 * 요청 본문은 없다.
 */
data class GuestFeedbackEntryResponseDto(
    @SerializedName("gate")
    val gate: GuestFeedbackGateDto,
    @SerializedName("requesterName")
    val requesterName: String?,
    @SerializedName("axes")
    val axes: List<GuestFeedbackAxisDto>?,
    @SerializedName("videoUrl")
    val videoUrl: String?,
    @SerializedName("submissionOpen")
    val submissionOpen: Boolean?,
)

data class GuestFeedbackAxisDto(
    @SerializedName("code")
    val code: GuestFeedbackAxisCodeDto,
    @SerializedName("displayName")
    val displayName: String,
)

/** Guest Feedback 공유 링크의 접근 가능 상태를 표현하는 서버 확정 wire 값이다. */
enum class GuestFeedbackGateDto {
    OPEN,
    PRIVATE,
    EXPIRED,
    FULL,
    ALREADY_SUBMITTED,
}

/** 지인이 평가하는 면접 태도 항목의 서버 확정 wire 값이다. */
enum class GuestFeedbackAxisCodeDto {
    GAZE,
    EXPRESSION,
    POSTURE,
    GESTURE,
    VOICE,
}

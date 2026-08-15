@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.feedback

import com.google.gson.annotations.SerializedName
import java.time.Instant

/** POST api/v1/feedback/guest/{token}/submissions */
data class GuestFeedbackSubmitRequestDto(
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("ratings")
    val ratings: List<GuestFeedbackRatingDto>,
)

data class GuestFeedbackRatingDto(
    @SerializedName("axis")
    val axis: GuestFeedbackAxisCodeDto,
    @SerializedName("level")
    val level: Int,
    @SerializedName("comment")
    val comment: String?,
)

data class GuestFeedbackSubmitResponseDto(
    @SerializedName("submissionId")
    val submissionId: Long,
    @SerializedName("submittedAt")
    val submittedAt: Instant,
)

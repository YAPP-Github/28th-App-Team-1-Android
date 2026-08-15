@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.feedback

import com.google.gson.annotations.SerializedName

/** POST api/v1/feedback/sessions/{sessionId}/share */
data class FeedbackShareCreateRequestDto(
    @SerializedName("axes")
    val axes: List<String>,
)

data class FeedbackShareCreateResponseDto(
    @SerializedName("token")
    val token: String?,
)

@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewVideoExpiry
import com.google.gson.annotations.SerializedName

/** GET api/v1/interview/sessions/{sessionId}/video/expiry */
data class InterviewVideoExpiryResponseDto(
    @SerializedName("expiresInSeconds")
    val expiresInSeconds: Long,
    @SerializedName("expired")
    val expired: Boolean,
) {
    fun toDomain(): InterviewVideoExpiry =
        InterviewVideoExpiry(
            expiresInSeconds = expiresInSeconds,
            expired = expired,
        )
}

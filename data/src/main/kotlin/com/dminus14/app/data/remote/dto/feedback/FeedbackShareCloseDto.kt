@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.feedback

import com.google.gson.annotations.SerializedName

/**
 * PATCH api/v1/feedback/sessions/{sessionId}/share
 *
 * 응답 본문은 없다.
 */
data class FeedbackShareCloseRequestDto(
    @SerializedName("status")
    val status: String = "PRIVATE",
)

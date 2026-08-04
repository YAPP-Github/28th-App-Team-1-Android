@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewVideoUploadUrl
import com.google.gson.annotations.SerializedName

/** POST api/v1/interview/sessions/{sessionId}/video/upload-url */
data class InterviewVideoUploadUrlResponseDto(
    @SerializedName("uploadUrl")
    val uploadUrl: String,
    @SerializedName("contentType")
    val contentType: String,
    @SerializedName("expiresInSeconds")
    val expiresInSeconds: Long,
) {
    fun toDomain(): InterviewVideoUploadUrl =
        InterviewVideoUploadUrl(
            uploadUrl = uploadUrl,
            contentType = contentType,
            expiresInSeconds = expiresInSeconds,
        )
}

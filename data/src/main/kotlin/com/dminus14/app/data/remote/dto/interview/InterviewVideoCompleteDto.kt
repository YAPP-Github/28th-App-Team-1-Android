@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/interview/sessions/{sessionId}/video/complete
 *
 * 응답 본문은 없다.
 */
data class InterviewVideoCompleteRequestDto(
    @SerializedName("wrapUpStartSec")
    val wrapUpStartSec: Float? = null,
    @SerializedName("wrapUpEndSec")
    val wrapUpEndSec: Float? = null,
)

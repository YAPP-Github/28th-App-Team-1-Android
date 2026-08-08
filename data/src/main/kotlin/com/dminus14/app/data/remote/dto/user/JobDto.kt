@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.user

import com.google.gson.annotations.SerializedName

/** GET api/v1/jobs 응답 (`JobListHttpResponse`). */
data class JobListResponseDto(
    @SerializedName("jobs")
    val jobs: List<JobDto>,
)

data class JobDto(
    @SerializedName("jobId")
    val jobId: Int,
    @SerializedName("jobRole")
    val jobRole: String,
    @SerializedName("label")
    val label: String,
)

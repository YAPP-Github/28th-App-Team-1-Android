@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.job

import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/jobs
 *
 * 요청 본문은 없다.
 */
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

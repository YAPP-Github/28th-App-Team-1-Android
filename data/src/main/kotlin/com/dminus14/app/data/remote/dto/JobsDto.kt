package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/** TODO(temp): 세션 재발급 검증용. 검증 후 삭제. */
data class JobsResponseDto(
    @SerializedName("jobs")
    val jobs: List<JobDto>,
)

/** TODO(temp): 세션 재발급 검증용. 검증 후 삭제. */
data class JobDto(
    @SerializedName("jobId")
    val jobId: Long,
    @SerializedName("jobRole")
    val jobRole: String,
    @SerializedName("label")
    val label: String,
)

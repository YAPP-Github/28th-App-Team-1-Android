package com.dminus14.app.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserProfileUpdateRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("jobRole")
    val jobRole: String,
    @SerializedName("careerYears")
    val careerYears: Int,
)

data class UserProfileUpdateResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: String? = null,
)

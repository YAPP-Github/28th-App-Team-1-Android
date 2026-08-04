package com.dminus14.app.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserProfileFetchRequestDto(
    @SerializedName("unused")
    val unused: Unit? = null,
)

data class UserProfileFetchResponseDto(
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("provider")
    val provider: String? = null,
    @SerializedName("jobRole")
    val jobRole: String? = null,
    @SerializedName("jobRoleLabel")
    val jobRoleLabel: String? = null,
    @SerializedName("careerYears")
    val careerYears: Int? = null,
    @SerializedName("remainingTicketCount")
    val remainingTicketCount: Int? = null,
)

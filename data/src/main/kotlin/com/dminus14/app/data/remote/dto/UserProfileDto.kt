package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserProfileDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("jobRole")
    val jobRole: String,
    @SerializedName("jobRoleLabel")
    val jobRoleLabel: String,
    @SerializedName("careerYears")
    val careerYears: Int,
    @SerializedName("remainingTicketCount")
    val remainingTicketCount: Int,
)

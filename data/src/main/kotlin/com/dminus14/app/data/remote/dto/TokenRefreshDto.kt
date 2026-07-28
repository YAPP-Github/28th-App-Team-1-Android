package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TokenRefreshRequestDto(
    @SerializedName("refreshToken")
    val refreshToken: String,
)

data class TokenRefreshResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

package com.dminus14.app.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/** POST api/v1/auth/token/refresh */
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

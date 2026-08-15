package com.dminus14.app.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/** POST api/v1/auth/social/login */
data class SocialLoginRequestDto(
    @SerializedName("provider")
    val provider: String = "KAKAO",
    @SerializedName("credential")
    val credential: String,
)

data class SocialLoginResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

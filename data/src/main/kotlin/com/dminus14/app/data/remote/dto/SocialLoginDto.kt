package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

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

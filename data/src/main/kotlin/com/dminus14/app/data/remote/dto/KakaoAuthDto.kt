package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class KakaoAuthRequestDto(
    @SerializedName("kakaoAccessToken")
    val kakaoAccessToken: String,
)

data class KakaoAuthResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

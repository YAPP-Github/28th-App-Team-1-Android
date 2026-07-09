package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.KakaoAuthRequestDto
import com.dminus14.app.data.remote.dto.KakaoAuthResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface KakaoAuthApi {
    @POST("auth/kakao")
    suspend fun loginWithKakao(
        @Body request: KakaoAuthRequestDto,
    ): KakaoAuthResponseDto
}

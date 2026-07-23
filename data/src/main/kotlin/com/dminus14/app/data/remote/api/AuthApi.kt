package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.SocialLoginRequestDto
import com.dminus14.app.data.remote.dto.SocialLoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/social/login")
    suspend fun loginWithSocial(
        @Body request: SocialLoginRequestDto,
    ): SocialLoginResponseDto
}

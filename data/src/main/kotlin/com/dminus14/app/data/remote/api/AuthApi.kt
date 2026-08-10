package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.SocialLoginRequestDto
import com.dminus14.app.data.remote.dto.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.TokenRefreshRequestDto
import com.dminus14.app.data.remote.dto.TokenRefreshResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/social/login")
    suspend fun loginWithSocial(
        @Body request: SocialLoginRequestDto,
    ): ApiResponseDto<SocialLoginResponseDto>

    @POST("api/v1/auth/token/refresh")
    suspend fun refreshToken(
        @Body request: TokenRefreshRequestDto,
    ): ApiResponseDto<TokenRefreshResponseDto>

    /** DELETE api/v1/auth/logout */
    @DELETE("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>
}

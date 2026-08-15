package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.auth.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.auth.TokenRefreshResponseDto

interface AuthRemoteDataSource {
    suspend fun loginWithKakao(credential: String): SocialLoginResponseDto

    suspend fun refreshToken(refreshToken: String): TokenRefreshResponseDto

    suspend fun logout()
}

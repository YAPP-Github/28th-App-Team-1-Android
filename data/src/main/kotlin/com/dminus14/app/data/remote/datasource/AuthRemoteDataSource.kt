package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.TokenRefreshResponseDto

interface AuthRemoteDataSource {
    suspend fun loginWithKakao(credential: String): SocialLoginResponseDto

    suspend fun refreshToken(refreshToken: String): TokenRefreshResponseDto
}

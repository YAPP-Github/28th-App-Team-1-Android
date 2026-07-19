package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.SocialLoginResponseDto

interface AuthRemoteDataSource {
    suspend fun loginWithKakao(credential: String): SocialLoginResponseDto
}

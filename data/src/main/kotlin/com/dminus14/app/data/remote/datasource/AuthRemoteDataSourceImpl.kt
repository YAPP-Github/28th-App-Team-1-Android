package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.dto.auth.SocialLoginRequestDto
import com.dminus14.app.data.remote.dto.auth.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.auth.TokenRefreshRequestDto
import com.dminus14.app.data.remote.dto.auth.TokenRefreshResponseDto
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSourceImpl
    @Inject
    constructor(
        private val authApi: AuthApi,
    ) : AuthRemoteDataSource {
        override suspend fun loginWithKakao(credential: String): SocialLoginResponseDto {
            val response =
                authApi.loginWithSocial(
                    SocialLoginRequestDto(
                        provider = PROVIDER_KAKAO,
                        credential = credential,
                    ),
                )
            return response.data ?: error("로그인 응답에 토큰이 없습니다.")
        }

        override suspend fun refreshToken(refreshToken: String): TokenRefreshResponseDto {
            val response = authApi.refreshToken(TokenRefreshRequestDto(refreshToken = refreshToken))
            return response.data ?: error("토큰 재발급 응답에 토큰이 없습니다.")
        }

        override suspend fun logout() {
            val response = authApi.logout()
            if (!response.isSuccessful) throw HttpException(response)
        }

        private companion object {
            const val PROVIDER_KAKAO = "KAKAO"
        }
    }

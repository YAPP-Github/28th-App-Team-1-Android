package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.dto.SocialLoginRequestDto
import com.dminus14.app.data.remote.dto.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.TokenRefreshRequestDto
import com.dminus14.app.data.remote.dto.TokenRefreshResponseDto
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
            authApi.logout()
        }

        private companion object {
            const val PROVIDER_KAKAO = "KAKAO"
        }
    }

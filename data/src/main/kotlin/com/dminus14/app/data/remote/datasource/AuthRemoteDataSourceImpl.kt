package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.dto.SocialLoginRequestDto
import com.dminus14.app.data.remote.dto.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.TokenRefreshRequestDto
import com.dminus14.app.data.remote.dto.TokenRefreshResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorBodyParser
import com.dminus14.app.data.remote.mapper.SocialLoginErrorMapper
import com.dminus14.app.domain.exception.LoginExpiredException
import com.dminus14.app.domain.exception.SocialLoginException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSourceImpl
    @Inject
    constructor(
        private val authApi: AuthApi,
    ) : AuthRemoteDataSource {
        override suspend fun loginWithKakao(credential: String): SocialLoginResponseDto =
            try {
                val response =
                    authApi.loginWithSocial(
                        SocialLoginRequestDto(
                            provider = PROVIDER_KAKAO,
                            credential = credential,
                        ),
                    )
                response.data
                    ?: throw SocialLoginException.Unknown(message = "로그인 응답에 토큰이 없습니다.")
            } catch (error: IOException) {
                throw SocialLoginException.Network(cause = error)
            } catch (error: HttpException) {
                throw SocialLoginErrorMapper.mapHttpException(error)
            }

        override suspend fun refreshToken(refreshToken: String): TokenRefreshResponseDto =
            try {
                val response = authApi.refreshToken(TokenRefreshRequestDto(refreshToken = refreshToken))
                response.data
                    ?: throw IllegalStateException("토큰 재발급 응답에 토큰이 없습니다.")
            } catch (error: HttpException) {
                if (ApiErrorBodyParser.isLoginExpired(error)) {
                    throw LoginExpiredException(cause = error)
                }
                // VALIDATION_ERROR 등 그 외는 그대로 전파한다.
                throw error
            }

        private companion object {
            const val PROVIDER_KAKAO = "KAKAO"
        }
    }

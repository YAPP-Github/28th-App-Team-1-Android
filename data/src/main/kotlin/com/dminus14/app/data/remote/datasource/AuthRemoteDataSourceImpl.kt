package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.dto.SocialLoginRequestDto
import com.dminus14.app.data.remote.dto.SocialLoginResponseDto
import com.dminus14.app.data.remote.dto.TokenRefreshRequestDto
import com.dminus14.app.data.remote.dto.TokenRefreshResponseDto
import com.dminus14.app.data.remote.mapper.SocialLoginErrorMapper
import com.dminus14.app.domain.model.KakaoAuthException
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
                authApi.loginWithSocial(
                    SocialLoginRequestDto(
                        provider = PROVIDER_KAKAO,
                        credential = credential,
                    ),
                )
            } catch (error: IOException) {
                throw KakaoAuthException.Network(cause = error)
            } catch (error: HttpException) {
                throw SocialLoginErrorMapper.mapHttpException(error)
            }

        override suspend fun refreshToken(refreshToken: String): TokenRefreshResponseDto =
            authApi.refreshToken(TokenRefreshRequestDto(refreshToken = refreshToken))

        private companion object {
            const val PROVIDER_KAKAO = "KAKAO"
        }
    }

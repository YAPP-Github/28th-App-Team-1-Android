package com.dminus14.app.data.repository

import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.dto.SocialLoginRequestDto
import com.dminus14.app.data.remote.mapper.SocialLoginErrorMapper
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.model.KakaoAuthException
import com.dminus14.app.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val authApi: AuthApi,
        private val localDataSource: LocalDataSource,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): Result<AuthSession> =
            try {
                Result.success(loginWithKakaoInternal(credential))
            } catch (error: KakaoAuthException) {
                Result.failure(error)
            }

        override suspend fun getAuthSession(): AuthSession? {
            val accessToken = localDataSource.getString(DataStoreKeys.Auth.ACCESS_TOKEN)
            val refreshToken = localDataSource.getString(DataStoreKeys.Auth.REFRESH_TOKEN)

            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                return null
            }

            return AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }

        override suspend fun clearAuthSession() {
            localDataSource.remove(DataStoreKeys.Auth.ACCESS_TOKEN)
            localDataSource.remove(DataStoreKeys.Auth.REFRESH_TOKEN)
        }

        private suspend fun loginWithKakaoInternal(credential: String): AuthSession {
            val response =
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

            localDataSource.setString(DataStoreKeys.Auth.ACCESS_TOKEN, response.accessToken)
            localDataSource.setString(DataStoreKeys.Auth.REFRESH_TOKEN, response.refreshToken)

            return AuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        private companion object {
            const val PROVIDER_KAKAO = "KAKAO"
        }
    }

package com.dminus14.app.data.repository

import com.dminus14.app.core.crypto.CryptoManager
import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import com.dminus14.app.data.local.datasource.PreferenceEdit
import com.dminus14.app.data.remote.auth.AccessTokenProvider
import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.data.remote.mapper.ApiErrorBodyParser
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.SessionException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.SessionRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val localDataSource: LocalDataSource,
        private val cryptoManager: CryptoManager,
        private val accessTokenProvider: AccessTokenProvider,
    ) : SessionRepository {
        override suspend fun getAuthSession(): AuthSession? {
            val snapshot = localDataSource.snapshot()
            val encryptedAccessToken = snapshot[DataStoreKeys.Auth.ACCESS_TOKEN]
            val encryptedRefreshToken = snapshot[DataStoreKeys.Auth.REFRESH_TOKEN]
            if (encryptedAccessToken == null || encryptedRefreshToken == null) return null
            return runCatching {
                AuthSession(
                    accessToken = cryptoManager.decryptStringFromBase64(encryptedAccessToken),
                    refreshToken = cryptoManager.decryptStringFromBase64(encryptedRefreshToken),
                )
            }.getOrElse { error ->
                when (error) {
                    is SecurityException,
                    is IllegalStateException,
                    is IllegalArgumentException,
                    -> {
                        clearAuthSession()
                        null
                    }

                    else -> {
                        throw error
                    }
                }
            }
        }

        override suspend fun refreshToken(refreshToken: String): AuthSession {
            val response =
                runCatching { authRemoteDataSource.refreshToken(refreshToken) }
                    .getOrElse { error -> throw mapRefreshFailure(error) }

            return saveAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        private fun mapRefreshFailure(error: Throwable): Throwable =
            when (error) {
                is IOException -> {
                    NetworkUnavailableException(
                        errCode = ApiErrorCode.NETWORK_UNAVAILABLE,
                        cause = error,
                    )
                }

                is HttpException -> {
                    toRefreshException(error)
                }

                is IllegalStateException -> {
                    UnknownException(
                        errCode = ApiErrorCode.UNKNOWN,
                        message = error.message ?: "알 수 없는 오류가 발생했습니다.",
                        cause = error,
                    )
                }

                else -> {
                    error
                }
            }

        private fun toRefreshException(error: HttpException): CustomException {
            val apiError = ApiErrorBodyParser.parse(error)
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.LOGIN_EXPIRED -> {
                    SessionException(
                        errCode = ApiErrorCode.LOGIN_EXPIRED,
                        message =
                            message.ifBlank {
                                "로그인 세션이 만료되었습니다. 다시 로그인해 주세요."
                            },
                        cause = error,
                    )
                }

                ApiErrorCode.VALIDATION_ERROR -> {
                    ValidationException(
                        errCode = ApiErrorCode.VALIDATION_ERROR,
                        message = message.ifBlank { "요청 값이 올바르지 않습니다." },
                        cause = error,
                    )
                }

                else -> {
                    when (error.code()) {
                        in HTTP_SERVER_ERROR_RANGE -> {
                            ServerException(
                                errCode = apiError?.code ?: ApiErrorCode.SERVER_ERROR,
                                cause = error,
                            )
                        }

                        else -> {
                            UnknownException(
                                errCode = apiError?.code ?: ApiErrorCode.UNKNOWN,
                                message = message.ifBlank { "알 수 없는 오류가 발생했습니다." },
                                cause = error,
                            )
                        }
                    }
                }
            }
        }

        override suspend fun saveAuthSession(
            accessToken: String,
            refreshToken: String,
        ): AuthSession {
            localDataSource.editAtomically(
                listOf(
                    PreferenceEdit.Set(
                        DataStoreKeys.Auth.ACCESS_TOKEN,
                        cryptoManager.encryptStringToBase64(accessToken),
                    ),
                    PreferenceEdit.Set(
                        DataStoreKeys.Auth.REFRESH_TOKEN,
                        cryptoManager.encryptStringToBase64(refreshToken),
                    ),
                ),
            )
            accessTokenProvider.set(accessToken)
            return AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }

        override suspend fun clearAuthSession() {
            localDataSource.editAtomically(
                listOf(
                    PreferenceEdit.Remove(DataStoreKeys.Auth.ACCESS_TOKEN),
                    PreferenceEdit.Remove(DataStoreKeys.Auth.REFRESH_TOKEN),
                ),
            )
            accessTokenProvider.clear()
        }

        private companion object {
            val HTTP_SERVER_ERROR_RANGE = 500..599
        }
    }

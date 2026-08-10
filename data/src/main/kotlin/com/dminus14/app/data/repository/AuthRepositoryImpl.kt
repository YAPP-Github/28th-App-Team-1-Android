package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.InvalidCredentialException
import com.dminus14.app.domain.exception.SocialLoginFailedException
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val sessionRepository: SessionRepository,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): AuthSession {
            val response =
                runCatching { authRemoteDataSource.loginWithKakao(credential) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                            val message = apiError?.message.orEmpty()
                            when (apiError?.code) {
                                ApiErrorCode.INVALID_CREDENTIAL -> {
                                    InvalidCredentialException(
                                        errCode = ApiErrorCode.INVALID_CREDENTIAL,
                                        message = message.ifBlank { "유효하지 않은 인증 정보입니다." },
                                        cause = httpError,
                                    )
                                }

                                ApiErrorCode.SOCIAL_LOGIN_FAILED -> {
                                    SocialLoginFailedException(
                                        errCode = ApiErrorCode.SOCIAL_LOGIN_FAILED,
                                        message = message.ifBlank { "소셜 로그인에 실패했습니다." },
                                        cause = httpError,
                                    )
                                }

                                else -> {
                                    null
                                }
                            }
                        }
                    }

            return sessionRepository.saveAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        override suspend fun logout() {
            runCatching { authRemoteDataSource.logout() }
                .getOrElse { error -> throw CommonApiErrorMapper.map(error) }
        }
    }

package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.data.remote.mapper.ApiErrorBodyParser
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.InvalidCredentialException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.SocialLoginFailedException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.repository.SessionRepository
import retrofit2.HttpException
import java.io.IOException
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
                try {
                    authRemoteDataSource.loginWithKakao(credential)
                } catch (error: IOException) {
                    throw NetworkUnavailableException(cause = error)
                } catch (error: HttpException) {
                    val apiError = ApiErrorBodyParser.parse(error)
                    val message = apiError?.message.orEmpty()
                    when (apiError?.code) {
                        ApiErrorCode.INVALID_CREDENTIAL -> {
                            throw InvalidCredentialException(
                                message = message.ifBlank { "유효하지 않은 인증 정보입니다." },
                                cause = error,
                            )
                        }

                        ApiErrorCode.SOCIAL_LOGIN_FAILED -> {
                            throw SocialLoginFailedException(
                                message = message.ifBlank { "소셜 로그인에 실패했습니다." },
                                cause = error,
                            )
                        }

                        else -> {
                            when (error.code()) {
                                in HTTP_SERVER_ERROR_RANGE -> throw ServerException(cause = error)
                                else -> {
                                    throw UnknownException(
                                        message = message.ifBlank { "알 수 없는 오류가 발생했습니다." },
                                        cause = error,
                                    )
                                }
                            }
                        }
                    }
                } catch (error: IllegalStateException) {
                    throw UnknownException(
                        message = error.message ?: "알 수 없는 오류가 발생했습니다.",
                        cause = error,
                    )
                }

            return sessionRepository.saveAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        private companion object {
            val HTTP_SERVER_ERROR_RANGE = 500..599
        }
    }

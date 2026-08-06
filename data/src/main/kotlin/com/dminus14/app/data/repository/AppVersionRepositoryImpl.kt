package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.AppVersionRemoteDataSource
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.InvalidPlatformException
import com.dminus14.app.domain.exception.InvalidVersionFormatException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.AppVersionPolicy
import com.dminus14.app.domain.repository.AppVersionRepository
import javax.inject.Inject
import javax.inject.Singleton

/** 앱 버전 정책 원격 DTO를 Domain 계약으로 변환하는 Repository 구현이다. */
@Singleton
class AppVersionRepositoryImpl
    @Inject
    constructor(
        private val appVersionRemoteDataSource: AppVersionRemoteDataSource,
    ) : AppVersionRepository {
        /**
         * [version]의 업데이트 정책을 조회한다.
         *
         * `APP_VERSION_POLICY_NOT_FOUND`(404)는 일반적인 4xx→ClientError 분류를 따르지 않고
         * [ServerException]으로 격상한다. 해당 플랫폼의 버전 정책 자체가 서버에 설정되지 않은
         * 상태로, 사용자가 화면에서 직접 대응할 방법이 없는 서버 설정 누락이므로 전역
         * App-exit Modal 처리로 넘긴다.
         */
        override suspend fun checkAppVersion(version: String): AppVersionPolicy {
            val response =
                runCatching {
                    appVersionRemoteDataSource.checkAppVersion(
                        platform = PLATFORM,
                        version = version,
                    )
                }.getOrElse { error ->
                    throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                        val message = apiError?.message.orEmpty()
                        when (apiError?.code) {
                            ApiErrorCode.INVALID_PLATFORM -> {
                                InvalidPlatformException(
                                    errCode = ApiErrorCode.INVALID_PLATFORM,
                                    message = message.ifBlank { "지원하지 않는 플랫폼입니다." },
                                    cause = httpError,
                                )
                            }

                            ApiErrorCode.INVALID_VERSION_FORMAT -> {
                                InvalidVersionFormatException(
                                    errCode = ApiErrorCode.INVALID_VERSION_FORMAT,
                                    message = message.ifBlank { "잘못된 버전 형식입니다." },
                                    cause = httpError,
                                )
                            }

                            ApiErrorCode.APP_VERSION_POLICY_NOT_FOUND -> {
                                ServerException(
                                    errCode = ApiErrorCode.APP_VERSION_POLICY_NOT_FOUND,
                                    message = message.ifBlank { "버전 정책을 확인할 수 없습니다." },
                                    cause = httpError,
                                )
                            }

                            else -> {
                                null
                            }
                        }
                    }
                }
            return response.toDomain()
        }

        private companion object {
            const val PLATFORM = "ANDROID"
        }
    }

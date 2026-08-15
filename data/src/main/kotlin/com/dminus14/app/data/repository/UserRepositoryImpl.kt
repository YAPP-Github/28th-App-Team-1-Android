package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.UserRemoteDataSource
import com.dminus14.app.data.remote.dto.common.ApiErrorResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.data.remote.mapper.toDomain
import com.dminus14.app.data.remote.mapper.toDto
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.InvalidJobRoleException
import com.dminus14.app.domain.exception.NameAlreadyTakenException
import com.dminus14.app.domain.exception.SocialReconnectRequiredException
import com.dminus14.app.domain.exception.SocialUnlinkFailedException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.Job
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate
import com.dminus14.app.domain.repository.UserRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/** User API DTO 변환과 비즈니스 오류 매핑을 담당하는 Repository 구현이다. */
@Singleton
class UserRepositoryImpl
    @Inject
    constructor(
        private val userRemoteDataSource: UserRemoteDataSource,
    ) : UserRepository {
        override suspend fun getUserProfile(): UserProfile =
            runCatching { userRemoteDataSource.getUserProfile() }
                .getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapUserNotFound)
                }.toDomain()

        override suspend fun updateUserProfile(update: UserProfileUpdate) {
            runCatching { userRemoteDataSource.updateUserProfile(update.toDto()) }
                .getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapUpdateError)
                }
        }

        override suspend fun withdraw() {
            runCatching { userRemoteDataSource.withdraw() }
                .getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapWithdrawalError)
                }
        }

        override suspend fun getJobList(): List<Job> =
            runCatching { userRemoteDataSource.getJobs() }
                .getOrElse { error ->
                    throw CommonApiErrorMapper.map(error)
                }.toDomain()

        private fun mapUserNotFound(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? =
            when (apiError?.code) {
                ApiErrorCode.USER_NOT_FOUND -> {
                    UserNotFoundException(
                        errCode = ApiErrorCode.USER_NOT_FOUND,
                        message = apiError.message.ifBlank { "존재하지 않는 사용자입니다." },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }

        private fun mapUpdateError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.VALIDATION_ERROR -> {
                    ValidationException(
                        errCode = ApiErrorCode.VALIDATION_ERROR,
                        message = message.ifBlank { "요청 값이 올바르지 않습니다." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.INVALID_JOB_ROLE -> {
                    InvalidJobRoleException(
                        errCode = ApiErrorCode.INVALID_JOB_ROLE,
                        message = message.ifBlank { "지원하지 않는 직무입니다." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.NAME_ALREADY_TAKEN -> {
                    NameAlreadyTakenException(
                        errCode = ApiErrorCode.NAME_ALREADY_TAKEN,
                        message = message.ifBlank { "이미 사용 중인 이름입니다." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.USER_NOT_FOUND -> {
                    mapUserNotFound(httpError, apiError)
                }

                else -> {
                    null
                }
            }
        }

        private fun mapWithdrawalError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.USER_NOT_FOUND -> {
                    mapUserNotFound(httpError, apiError)
                }

                ApiErrorCode.SOCIAL_RECONNECT_REQUIRED -> {
                    SocialReconnectRequiredException(
                        errCode = ApiErrorCode.SOCIAL_RECONNECT_REQUIRED,
                        message = message.ifBlank { "다시 로그인한 뒤 탈퇴해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.SOCIAL_UNLINK_FAILED -> {
                    SocialUnlinkFailedException(
                        errCode = ApiErrorCode.SOCIAL_UNLINK_FAILED,
                        message = message.ifBlank { "소셜 연결 해제에 실패했습니다." },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }
    }

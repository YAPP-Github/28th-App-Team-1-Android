package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.UserRemoteDataSource
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
@Inject
constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {
    override suspend fun getUserProfile(): UserProfile {
        val response =
            runCatching { userRemoteDataSource.getUserProfile() }
                .getOrElse { error ->
                    throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                        val message = apiError?.message.orEmpty()
                        when (apiError?.code) {
                            ApiErrorCode.USER_NOT_FOUND -> {
                                UserNotFoundException(
                                    errCode = ApiErrorCode.USER_NOT_FOUND,
                                    message = message.ifBlank { "존재하지 않는 사용자입니다." },
                                    cause = httpError,
                                )
                            }

                            else -> {
                                UnknownException(
                                    errCode = ApiErrorCode.UNKNOWN,
                                    message = message.ifBlank { "알 수 없는 오류가 발생했습니다." },
                                    cause = error,
                                )
                            }
                        }
                    }
                }

        return UserProfile(
            name = response.name,
            email = response.email,
            provider = response.provider,
            jobRole = response.jobRole,
            jobRoleLabel = response.jobRoleLabel,
            careerYears = response.careerYears,
            remainingTicketCount = response.remainingTicketCount,
        )
    }
}

package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.UserApi
import com.dminus14.app.data.remote.dto.UserProfileDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSourceImpl
    @Inject
    constructor(
        private val userApi: UserApi,
    ) : UserRemoteDataSource {
        override suspend fun getUserProfile(): UserProfileDto {
            val response = userApi.getProfile()
            return UserProfileDto(
                name = response.name.orEmpty(),
                email = response.email.orEmpty(),
                provider = response.provider.orEmpty(),
                jobRole = response.jobRole.orEmpty(),
                jobRoleLabel = response.jobRoleLabel.orEmpty(),
                careerYears = response.careerYears ?: 0,
                remainingTicketCount = response.remainingTicketCount ?: 0,
            )
        }
    }

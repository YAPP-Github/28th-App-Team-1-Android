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
            val response = userApi.getUserProfile()
            return response.data ?: error("회원 프로필 응답이 비어 있습니다.")
        }
    }

package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.UserProfileDto

interface UserRemoteDataSource {
    suspend fun getUserProfile(): UserProfileDto
}

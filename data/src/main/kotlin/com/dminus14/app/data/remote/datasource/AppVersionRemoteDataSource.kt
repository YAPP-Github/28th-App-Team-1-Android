package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.AppVersionCheckResponseDto

interface AppVersionRemoteDataSource {
    suspend fun checkAppVersion(
        platform: String,
        version: String,
    ): AppVersionCheckResponseDto
}

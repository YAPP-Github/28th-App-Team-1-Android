package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.appversion.AppVersionCheckResponseDto

interface AppVersionRemoteDataSource {
    suspend fun checkAppVersion(
        platform: String,
        version: String,
    ): AppVersionCheckResponseDto
}

package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.AppVersionApi
import com.dminus14.app.data.remote.dto.AppVersionCheckResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVersionRemoteDataSourceImpl
    @Inject
    constructor(
        private val appVersionApi: AppVersionApi,
    ) : AppVersionRemoteDataSource {
        override suspend fun checkAppVersion(
            platform: String,
            version: String,
        ): AppVersionCheckResponseDto {
            val response = appVersionApi.checkAppVersion(platform = platform, version = version)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "앱 버전 정책 응답이 비어 있습니다.",
                )
        }
    }

package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.appversion.AppVersionCheckResponseDto
import com.dminus14.app.data.remote.dto.common.ApiResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** 앱 버전 정책 조회를 담당하는 Retrofit API다. */
interface AppVersionApi {
    /** GET api/v1/app-versions/check */
    @GET("api/v1/app-versions/check")
    suspend fun checkAppVersion(
        @Query("platform") platform: String,
        @Query("version") version: String,
    ): ApiResponseDto<AppVersionCheckResponseDto>
}

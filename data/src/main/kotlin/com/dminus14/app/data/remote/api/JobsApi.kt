package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.JobsResponseDto
import retrofit2.http.GET

/** TODO(temp): 세션 재발급 검증용. 검증 후 삭제. */
interface JobsApi {
    @GET("api/v1/jobs")
    suspend fun getJobs(): ApiResponseDto<JobsResponseDto>
}

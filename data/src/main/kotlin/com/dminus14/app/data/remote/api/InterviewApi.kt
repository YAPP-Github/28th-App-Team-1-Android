package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.JdValidateRequestDto
import com.dminus14.app.data.remote.dto.JdValidateResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InterviewApi {
    @POST("api/v1/jd/validate")
    suspend fun validateJdUrl(
        @Body request: JdValidateRequestDto,
    ): ApiResponseDto<JdValidateResponseDto>

    /**
     * 면접 세션 생성 접수.
     *
     * 성공 시 HTTP 202 + `data.status = PROCESSING`.
     */
    @POST("api/v1/interview/sessions")
    suspend fun createInterviewSession(
        @Body request: CreateInterviewSessionRequestDto,
    ): ApiResponseDto<InterviewSessionResponseDto>

    /**
     * 면접 세션 준비 상태 조회.
     *
     * 생성 후 3~5초 간격 폴링용.
     */
    @GET("api/v1/interview/sessions/{sessionId}/status")
    suspend fun getInterviewSessionStatus(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<InterviewSessionStatusResponseDto>
}

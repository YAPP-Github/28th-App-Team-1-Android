package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.common.ApiResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitRequestDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitResponseDto
import com.dminus14.app.data.remote.interceptor.InsertInstallationIdInterceptor
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/** 공유 token으로 비회원 피드백 진입 상태를 조회하고 평가를 제출하는 Retrofit API다. */
interface GuestFeedbackApi {
    /** 설치 ID를 함께 전송해 공유 링크의 진입 상태와 평가 대상 정보를 조회한다. */
    @Headers(
        "${InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED}: " +
            InsertInstallationIdInterceptor.INSTALLATION_ID_REQUIRED_VALUE,
    )
    @GET("api/v1/feedback/guest/{token}")
    suspend fun enter(
        @Path("token") token: String,
    ): ApiResponseDto<GuestFeedbackEntryResponseDto>

    /** 설치 ID와 nullable 평가 항목을 포함한 비회원 피드백을 제출한다. */
    @Headers(
        "${InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED}: " +
            InsertInstallationIdInterceptor.INSTALLATION_ID_REQUIRED_VALUE,
    )
    @POST("api/v1/feedback/guest/{token}/submissions")
    suspend fun submit(
        @Path("token") token: String,
        @Body request: GuestFeedbackSubmitRequestDto,
    ): ApiResponseDto<GuestFeedbackSubmitResponseDto>
}

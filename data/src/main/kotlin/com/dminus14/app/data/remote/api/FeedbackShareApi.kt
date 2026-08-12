package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareCreateRequestDto
import com.dminus14.app.data.remote.dto.FeedbackShareCreateResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareStatusResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareUpdateRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/** 로그인 사용자 토큰으로 지인 피드백 공유 링크를 생성·조회·비공개 전환하는 Retrofit API다. */
interface FeedbackShareApi {
    @GET("api/v1/feedback/sessions/{sessionId}/share")
    suspend fun getStatus(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<FeedbackShareStatusResponseDto>

    @POST("api/v1/feedback/sessions/{sessionId}/share")
    suspend fun create(
        @Path("sessionId") sessionId: Long,
        @Body request: FeedbackShareCreateRequestDto,
    ): ApiResponseDto<FeedbackShareCreateResponseDto>

    @PATCH("api/v1/feedback/sessions/{sessionId}/share")
    suspend fun makePrivate(
        @Path("sessionId") sessionId: Long,
        @Body request: FeedbackShareUpdateRequestDto,
    ): ApiResponseDto<Unit?>
}

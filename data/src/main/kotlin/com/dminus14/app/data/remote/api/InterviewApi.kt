package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.interview.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewReportListResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewReportResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewResumeConfirmResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewResumeStatusResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoCompleteRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoExpiryResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoUploadUrlResponseDto
import com.dminus14.app.data.remote.dto.interview.JdValidateRequestDto
import com.dminus14.app.data.remote.dto.interview.JdValidateResponseDto
import com.dminus14.app.data.remote.dto.interview.SubmitAnswerResponseDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

@Suppress("TooManyFunctions")
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

    @GET("api/v1/interview/sessions")
    suspend fun getReportList(): ApiResponseDto<InterviewReportListResponseDto>

    @Multipart
    @POST("api/v1/interview/sessions/{sessionId}/answers")
    @Suppress("LongParameterList")
    suspend fun submitAnswer(
        @Path("sessionId") sessionId: Long,
        @Query("questionId") questionId: Long,
        @Query("isWrapUp") isWrapUp: Boolean,
        @Query("questionAudioStartAt") questionAudioStartAt: Float? = null,
        @Query("questionAudioEndAt") questionAudioEndAt: Float? = null,
        @Query("answerStartAt") answerStartAt: Float? = null,
        @Query("answerEndAt") answerEndAt: Float? = null,
        @Query("answerDuration") answerDuration: Float? = null,
        @Query("endType") endType: String? = null,
        @Part audio: MultipartBody.Part? = null,
    ): ApiResponseDto<SubmitAnswerResponseDto>

    @Streaming
    @GET("api/v1/interview/sessions/{sessionId}/questions/{questionId}/audio/stream")
    suspend fun streamAudio(
        @Path("sessionId") sessionId: Long,
        @Path("questionId") questionId: Long,
    ): ResponseBody

    @GET("api/v1/interview/sessions/{sessionId}/resume")
    suspend fun getResume(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<InterviewResumeStatusResponseDto>

    @POST("api/v1/interview/sessions/{sessionId}/resume")
    suspend fun confirmResume(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<InterviewResumeConfirmResponseDto>

    @POST("api/v1/interview/sessions/{sessionId}/abandon")
    suspend fun abandon(
        @Path("sessionId") sessionId: Long,
        @Body request: InterviewAbandonRequestDto? = null,
    ): ApiResponseDto<InterviewAbandonResponseDto>

    @GET("api/v1/interview/sessions/{sessionId}/report")
    suspend fun getReport(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<InterviewReportResponseDto>

    @POST("api/v1/interview/sessions/{sessionId}/video/upload-url")
    suspend fun issueUploadUrl(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<InterviewVideoUploadUrlResponseDto>

    @POST("api/v1/interview/sessions/{sessionId}/video/complete")
    suspend fun completeUpload(
        @Path("sessionId") sessionId: Long,
        @Body request: InterviewVideoCompleteRequestDto? = null,
    ): ApiResponseDto<Unit>

    @GET("api/v1/interview/sessions/{sessionId}/video/expiry")
    suspend fun getExpiry(
        @Path("sessionId") sessionId: Long,
    ): ApiResponseDto<InterviewVideoExpiryResponseDto>
}

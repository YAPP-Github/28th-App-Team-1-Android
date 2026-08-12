package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.interview.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.interview.FeedbackShareCreateResponseDto
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
import com.dminus14.app.data.remote.dto.interview.JdValidateResponseDto
import com.dminus14.app.data.remote.dto.interview.SubmitAnswerResponseDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody

@Suppress("TooManyFunctions")
interface InterviewRemoteDataSource {
    suspend fun validateJdUrl(jdUrl: String): JdValidateResponseDto

    suspend fun createInterviewSession(
        request: CreateInterviewSessionRequestDto,
    ): InterviewSessionResponseDto

    suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatusResponseDto

    suspend fun getReportList(): InterviewReportListResponseDto

    @Suppress("LongParameterList")
    suspend fun submitAnswer(
        sessionId: Long,
        questionId: Long,
        isWrapUp: Boolean,
        questionAudioStartAt: Float? = null,
        questionAudioEndAt: Float? = null,
        answerStartAt: Float? = null,
        answerEndAt: Float? = null,
        answerDuration: Float? = null,
        endType: String? = null,
        audio: MultipartBody.Part? = null,
    ): SubmitAnswerResponseDto

    suspend fun streamAudio(
        sessionId: Long,
        questionId: Long,
    ): ResponseBody

    suspend fun getResume(sessionId: Long): InterviewResumeStatusResponseDto

    suspend fun confirmResume(sessionId: Long): InterviewResumeConfirmResponseDto

    suspend fun abandon(
        sessionId: Long,
        request: InterviewAbandonRequestDto? = null,
    ): InterviewAbandonResponseDto

    suspend fun getReport(sessionId: Long): InterviewReportResponseDto

    suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrlResponseDto

    suspend fun completeUpload(
        sessionId: Long,
        request: InterviewVideoCompleteRequestDto? = null,
    )

    suspend fun getExpiry(sessionId: Long): InterviewVideoExpiryResponseDto

    suspend fun createFeedbackShare(
        sessionId: Long,
        axes: List<String>,
    ): FeedbackShareCreateResponseDto
}

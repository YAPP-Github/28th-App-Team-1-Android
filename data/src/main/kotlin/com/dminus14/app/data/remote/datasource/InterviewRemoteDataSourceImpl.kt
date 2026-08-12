package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.InterviewApi
import com.dminus14.app.data.remote.dto.interview.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.interview.FeedbackShareCreateRequestDto
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
import com.dminus14.app.data.remote.dto.interview.JdValidateRequestDto
import com.dminus14.app.data.remote.dto.interview.JdValidateResponseDto
import com.dminus14.app.data.remote.dto.interview.SubmitAnswerResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("TooManyFunctions")
class InterviewRemoteDataSourceImpl
    @Inject
    constructor(
        private val interviewApi: InterviewApi,
    ) : InterviewRemoteDataSource {
        override suspend fun validateJdUrl(jdUrl: String): JdValidateResponseDto {
            val response = interviewApi.validateJdUrl(JdValidateRequestDto(jdUrl = jdUrl))
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "JD 유효성 검사 응답이 비어 있습니다.",
                )
        }

        override suspend fun createInterviewSession(
            request: CreateInterviewSessionRequestDto,
        ): InterviewSessionResponseDto {
            val response = interviewApi.createInterviewSession(request)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 세션 생성 응답이 비어 있습니다.",
                )
        }

        override suspend fun getInterviewSessionStatus(
            sessionId: Long,
        ): InterviewSessionStatusResponseDto {
            val response = interviewApi.getInterviewSessionStatus(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 세션 상태 응답이 비어 있습니다.",
                )
        }

        override suspend fun getReportList(): InterviewReportListResponseDto {
            val response = interviewApi.getReportList()
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 리포트 목록 응답이 비어 있습니다.",
                )
        }

        @Suppress("LongParameterList")
        override suspend fun submitAnswer(
            sessionId: Long,
            questionId: Long,
            isWrapUp: Boolean,
            questionAudioStartAt: Float?,
            questionAudioEndAt: Float?,
            answerStartAt: Float?,
            answerEndAt: Float?,
            answerDuration: Float?,
            endType: String?,
            audio: MultipartBody.Part?,
        ): SubmitAnswerResponseDto {
            val response =
                interviewApi.submitAnswer(
                    sessionId = sessionId,
                    questionId = questionId,
                    isWrapUp = isWrapUp,
                    questionAudioStartAt = questionAudioStartAt,
                    questionAudioEndAt = questionAudioEndAt,
                    answerStartAt = answerStartAt,
                    answerEndAt = answerEndAt,
                    answerDuration = answerDuration,
                    endType = endType,
                    audio = audio,
                )
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "답변 제출 응답이 비어 있습니다.",
                )
        }

        override suspend fun streamAudio(
            sessionId: Long,
            questionId: Long,
        ): ResponseBody = interviewApi.streamAudio(sessionId = sessionId, questionId = questionId)

        override suspend fun getResume(sessionId: Long): InterviewResumeStatusResponseDto {
            val response = interviewApi.getResume(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 재개 상태 응답이 비어 있습니다.",
                )
        }

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirmResponseDto {
            val response = interviewApi.confirmResume(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 재개 확정 응답이 비어 있습니다.",
                )
        }

        override suspend fun abandon(
            sessionId: Long,
            request: InterviewAbandonRequestDto?,
        ): InterviewAbandonResponseDto {
            val response = interviewApi.abandon(sessionId, request)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 중단 응답이 비어 있습니다.",
                )
        }

        override suspend fun getReport(sessionId: Long): InterviewReportResponseDto {
            val response = interviewApi.getReport(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "면접 리포트 응답이 비어 있습니다.",
                )
        }

        override suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrlResponseDto {
            val response = interviewApi.issueUploadUrl(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "비디오 업로드 URL 응답이 비어 있습니다.",
                )
        }

        override suspend fun completeUpload(
            sessionId: Long,
            request: InterviewVideoCompleteRequestDto?,
        ) {
            interviewApi.completeUpload(sessionId, request)
        }

        override suspend fun getExpiry(sessionId: Long): InterviewVideoExpiryResponseDto {
            val response = interviewApi.getExpiry(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "비디오 만료 시간 응답이 비어 있습니다.",
                )
        }

        override suspend fun createFeedbackShare(
            sessionId: Long,
            axes: List<String>,
        ): FeedbackShareCreateResponseDto {
            val response =
                interviewApi.createFeedbackShare(
                    sessionId = sessionId,
                    request = FeedbackShareCreateRequestDto(axes = axes),
                )
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "피드백 공유 링크 응답이 비어 있습니다.",
                )
        }
    }

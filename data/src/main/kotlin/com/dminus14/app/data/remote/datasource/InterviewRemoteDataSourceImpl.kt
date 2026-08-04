package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.InterviewApi
import com.dminus14.app.data.remote.dto.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.JdValidateRequestDto
import com.dminus14.app.data.remote.dto.JdValidateResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
    }

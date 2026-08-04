package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.JdValidateResponseDto

interface InterviewRemoteDataSource {
    suspend fun validateJdUrl(jdUrl: String): JdValidateResponseDto

    suspend fun createInterviewSession(
        request: CreateInterviewSessionRequestDto,
    ): InterviewSessionResponseDto

    suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatusResponseDto
}

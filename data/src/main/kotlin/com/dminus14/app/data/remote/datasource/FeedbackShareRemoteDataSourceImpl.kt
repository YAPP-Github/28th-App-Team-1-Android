package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.FeedbackShareApi
import com.dminus14.app.data.remote.dto.FeedbackShareCreateRequestDto
import com.dminus14.app.data.remote.dto.FeedbackShareCreateResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareStatusResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareUpdateRequestDto
import com.dminus14.app.data.remote.dto.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackShareRemoteDataSourceImpl
    @Inject
    constructor(
        private val feedbackShareApi: FeedbackShareApi,
    ) : FeedbackShareRemoteDataSource {
        override suspend fun getStatus(sessionId: Long): FeedbackShareStatusResponseDto {
            val response = feedbackShareApi.getStatus(sessionId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "공유 링크 상태 응답이 비어 있습니다.",
                )
        }

        override suspend fun create(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCodeDto>,
        ): FeedbackShareCreateResponseDto {
            val response =
                feedbackShareApi.create(
                    sessionId = sessionId,
                    request = FeedbackShareCreateRequestDto(axes = axes),
                )
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "공유 링크 생성 응답이 비어 있습니다.",
                )
        }

        override suspend fun makePrivate(sessionId: Long) {
            feedbackShareApi.makePrivate(
                sessionId = sessionId,
                request = FeedbackShareUpdateRequestDto(),
            )
        }
    }

package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.FeedbackShareRemoteDataSource
import com.dminus14.app.data.remote.dto.ApiErrorResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.data.remote.mapper.toDomain
import com.dminus14.app.data.remote.mapper.toShareAxisDto
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.EmptyAttitudeAxesException
import com.dminus14.app.domain.exception.FeedbackShareAlreadyExistsException
import com.dminus14.app.domain.exception.FeedbackShareNotFoundException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidAttitudeAxisException
import com.dminus14.app.domain.exception.InvalidShareStatusException
import com.dminus14.app.domain.exception.TooManyAttitudeAxesException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.FeedbackShare
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.google.gson.JsonParseException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FeedbackShare 원격 DTO를 Domain 계약으로 변환하는 Repository 구현이다.
 *
 * 비즈니스 오류는 여섯 Domain 의미로 구분하고 전송·서버·알 수 없는 오류는 기존 공통 변환
 * 정책을 사용한다.
 */
@Singleton
class FeedbackShareRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: FeedbackShareRemoteDataSource,
    ) : FeedbackShareRepository {
        override suspend fun getStatus(sessionId: Long): FeedbackShare =
            runCatching { remoteDataSource.getStatus(sessionId) }
                .getOrElse { error -> throw mapError(error) }
                .toDomain()

        override suspend fun create(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): String =
            runCatching {
                remoteDataSource.create(
                    sessionId = sessionId,
                    axes = axes.map { axis -> axis.toShareAxisDto() },
                )
            }.getOrElse { error -> throw mapError(error) }
                .token

        override suspend fun makePrivate(sessionId: Long) {
            runCatching { remoteDataSource.makePrivate(sessionId) }
                .getOrElse { error -> throw mapError(error) }
        }

        private fun mapError(error: Throwable): Throwable =
            if (error is JsonParseException) {
                UnknownException(
                    errCode = ApiErrorCode.UNKNOWN,
                    cause = error,
                )
            } else {
                CommonApiErrorMapper.map(error, ::mapBusinessError)
            }

        private fun mapBusinessError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val code = apiError?.code ?: return null
            val message = apiError.message
            return when (code) {
                ApiErrorCode.FEEDBACK_SHARE_NOT_FOUND -> {
                    FeedbackShareNotFoundException(code, message, httpError)
                }

                ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND -> {
                    InterviewSessionNotFoundException(code, message, httpError)
                }

                ApiErrorCode.FEEDBACK_SHARE_ALREADY_EXISTS -> {
                    FeedbackShareAlreadyExistsException(code, message, httpError)
                }

                ApiErrorCode.EMPTY_ATTITUDE_AXES -> {
                    EmptyAttitudeAxesException(code, message, httpError)
                }

                ApiErrorCode.TOO_MANY_ATTITUDE_AXES -> {
                    TooManyAttitudeAxesException(code, message, httpError)
                }

                ApiErrorCode.INVALID_ATTITUDE_AXIS -> {
                    InvalidAttitudeAxisException(code, message, httpError)
                }

                ApiErrorCode.INVALID_SHARE_STATUS -> {
                    InvalidShareStatusException(code, message, httpError)
                }

                else -> {
                    null
                }
            }
        }
    }

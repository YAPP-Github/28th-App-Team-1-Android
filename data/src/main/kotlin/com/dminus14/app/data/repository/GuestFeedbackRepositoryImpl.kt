package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.GuestFeedbackRemoteDataSource
import com.dminus14.app.data.remote.dto.ApiErrorResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.data.remote.mapper.toDomain
import com.dminus14.app.data.remote.mapper.toDto
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.GuestFeedbackAlreadySubmittedException
import com.dminus14.app.domain.exception.GuestFeedbackCapacityFullException
import com.dminus14.app.domain.exception.GuestFeedbackRequestException
import com.dminus14.app.domain.exception.GuestFeedbackShareClosedException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.repository.GuestFeedbackRepository
import com.google.gson.JsonParseException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guest Feedback 원격 DTO를 Domain 계약으로 변환하는 Repository 구현이다.
 *
 * 공유 token, 영상 정보와 작성 피드백은 저장하거나 로그로 남기지 않는다. Guest 비즈니스 오류는
 * 네 Domain 의미로 구분하고 전송·서버·알 수 없는 오류는 기존 공통 변환 정책을 사용한다.
 */
@Singleton
class GuestFeedbackRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: GuestFeedbackRemoteDataSource,
    ) : GuestFeedbackRepository {
        /** 진입 DTO를 작성 가능 또는 작성 불가 Domain 결과로 변환한다. */
        override suspend fun enter(token: String): GuestFeedbackEntry =
            runCatching { remoteDataSource.enter(token) }
                .getOrElse { error -> throw mapError(error) }
                .toDomain()

        /** 제출 입력을 wire DTO로 변환하며 성공 응답의 ID와 시각은 노출하지 않는다. */
        override suspend fun submit(
            token: String,
            submission: GuestFeedbackSubmission,
        ) {
            runCatching {
                remoteDataSource.submit(
                    token = token,
                    nickname = submission.nickname,
                    ratings = submission.ratings.map { rating -> rating.toDto() },
                )
            }.getOrElse { error -> throw mapError(error) }
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
                ApiErrorCode.FEEDBACK_SHARE_TOKEN_NOT_FOUND,
                ApiErrorCode.INCOMPLETE_RATINGS,
                ApiErrorCode.INVALID_RATING_LEVEL,
                ApiErrorCode.MISSING_DEVICE_ID,
                -> {
                    GuestFeedbackRequestException(code, message, httpError)
                }

                ApiErrorCode.FEEDBACK_SHARE_CLOSED -> {
                    GuestFeedbackShareClosedException(code, message, httpError)
                }

                ApiErrorCode.FEEDBACK_CAPACITY_FULL -> {
                    GuestFeedbackCapacityFullException(code, message, httpError)
                }

                ApiErrorCode.FEEDBACK_ALREADY_SUBMITTED -> {
                    GuestFeedbackAlreadySubmittedException(code, message, httpError)
                }

                else -> {
                    null
                }
            }
        }
    }

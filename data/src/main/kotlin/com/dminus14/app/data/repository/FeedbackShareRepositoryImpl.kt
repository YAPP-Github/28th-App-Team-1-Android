package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.dto.common.ApiErrorResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.EmptyAttitudeAxesException
import com.dminus14.app.domain.exception.FeedbackShareAlreadyExistsException
import com.dminus14.app.domain.exception.FeedbackShareNotFoundException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidAttitudeAxisException
import com.dminus14.app.domain.exception.InvalidFeedbackShareStatusException
import com.dminus14.app.domain.exception.TooManyAttitudeAxesException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 호스트 공유 링크 생성·종료 Repository 구현. 인증 Interview 원격 데이터 소스를 재사용한다.
 *
 * 이 구현 자체는 token 을 저장하거나 로그로 남기지 않고 그대로 상위 계층에 반환한다. 기기 저장은
 * `FeedbackShareLocalRepository` 가 맡는다.
 */
@Singleton
class FeedbackShareRepositoryImpl
    @Inject
    constructor(
        private val interviewRemoteDataSource: InterviewRemoteDataSource,
    ) : FeedbackShareRepository {
        override suspend fun createShare(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): String {
            val response =
                runCatching {
                    interviewRemoteDataSource.createFeedbackShare(
                        sessionId = sessionId,
                        axes = axes.map { it.name },
                    )
                }.getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapCreateShareError)
                }
            return response.token ?: throw UnknownException(errCode = ApiErrorCode.UNKNOWN)
        }

        override suspend fun closeShare(sessionId: Long) {
            runCatching {
                interviewRemoteDataSource.closeFeedbackShare(sessionId)
            }.getOrElse { error -> throw CommonApiErrorMapper.map(error, ::mapCloseShareError) }
        }

        /** POST `/api/v1/feedback/sessions/{sessionId}/share` (`create_1`) 전용 비즈니스 코드. */
        private fun mapCreateShareError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.EMPTY_ATTITUDE_AXES -> {
                    EmptyAttitudeAxesException(
                        errCode = ApiErrorCode.EMPTY_ATTITUDE_AXES,
                        message = message.ifBlank { "평가 항목을 최소 1개 선택해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.TOO_MANY_ATTITUDE_AXES -> {
                    TooManyAttitudeAxesException(
                        errCode = ApiErrorCode.TOO_MANY_ATTITUDE_AXES,
                        message = message.ifBlank { "평가 항목은 최대 5개까지 선택할 수 있어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.INVALID_ATTITUDE_AXIS -> {
                    InvalidAttitudeAxisException(
                        errCode = ApiErrorCode.INVALID_ATTITUDE_AXIS,
                        message = message.ifBlank { "지원하지 않는 평가 항목이에요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND -> {
                    InterviewSessionNotFoundException(
                        errCode = ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND,
                        message = message.ifBlank { "면접 세션을 찾을 수 없어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.FEEDBACK_SHARE_ALREADY_EXISTS -> {
                    FeedbackShareAlreadyExistsException(
                        errCode = ApiErrorCode.FEEDBACK_SHARE_ALREADY_EXISTS,
                        message = message.ifBlank { "이미 피드백 요청 링크가 있어요." },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }

        /** PATCH `/api/v1/feedback/sessions/{sessionId}/share` (`updateStatus`) 전용 비즈니스 코드. */
        private fun mapCloseShareError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.INVALID_SHARE_STATUS -> {
                    InvalidFeedbackShareStatusException(
                        errCode = ApiErrorCode.INVALID_SHARE_STATUS,
                        message = message.ifBlank { "지원하지 않는 상태 전환이에요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.FEEDBACK_SHARE_NOT_FOUND -> {
                    FeedbackShareNotFoundException(
                        errCode = ApiErrorCode.FEEDBACK_SHARE_NOT_FOUND,
                        message = message.ifBlank { "피드백 공유 링크를 찾을 수 없어요." },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }
    }

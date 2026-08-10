package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.config.NetworkConfig
import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.dto.ApiErrorResponseDto
import com.dminus14.app.data.remote.dto.interview.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoCompleteRequestDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.data.remote.uploader.InterviewVideoUploader
import com.dminus14.app.domain.exception.AiTemporarilyUnavailableException
import com.dminus14.app.domain.exception.AnswerAlreadySubmittedException
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.FreeTextNotRelevantException
import com.dminus14.app.domain.exception.InterviewSessionAlreadyEndedException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InterviewSessionNotStartedException
import com.dminus14.app.domain.exception.InterviewSessionPreloadFailedException
import com.dminus14.app.domain.exception.InvalidFreeTextLengthException
import com.dminus14.app.domain.exception.InvalidJdLengthException
import com.dminus14.app.domain.exception.InvalidJdUrlException
import com.dminus14.app.domain.exception.JdContentNotFoundException
import com.dminus14.app.domain.exception.JdNotValidatedException
import com.dminus14.app.domain.exception.JdUrlAndTextBothProvidedException
import com.dminus14.app.domain.exception.JdValidationLimitExceededException
import com.dminus14.app.domain.exception.NoRemainingTicketException
import com.dminus14.app.domain.exception.PortfolioNotFoundException
import com.dminus14.app.domain.exception.PortfolioProcessingException
import com.dminus14.app.domain.exception.PortfolioUploadFailedException
import com.dminus14.app.domain.exception.UserProfileNotRegisteredException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.model.InterviewAbandonRequestCause
import com.dminus14.app.domain.model.InterviewAnswerEndRequest
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportList
import com.dminus14.app.domain.model.InterviewResumeConfirm
import com.dminus14.app.domain.model.InterviewResumeStatus
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewVideoExpiry
import com.dminus14.app.domain.model.InterviewVideoUploadUrl
import com.dminus14.app.domain.model.JdValidationResult
import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.model.SubmitInterviewAnswerCommand
import com.dminus14.app.domain.model.UploadInterviewVideoCommand
import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 면접(JD 검증·세션 생성·세션 상태·답변 제출 등) 원격 DTO를 Domain 계약으로 변환하는 Repository 구현이다.
 *
 * API별 비즈니스 오류는 각 함수에서만 매핑하고, 전송·서버·알 수 없는 오류는
 * 공통 변환 정책([CommonApiErrorMapper])을 사용한다.
 */
@Singleton
@Suppress("TooManyFunctions")
class InterviewRepositoryImpl
    @Inject
    constructor(
        private val interviewRemoteDataSource: InterviewRemoteDataSource,
        private val interviewLocalRepository: InterviewLocalRepository? = null,
        private val interviewVideoUploader: InterviewVideoUploader? = null,
    ) : InterviewRepository {
        override suspend fun validateJdUrl(jdUrl: String): JdValidationResult {
            val response =
                runCatching { interviewRemoteDataSource.validateJdUrl(jdUrl) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                            val message = apiError?.message.orEmpty()
                            when (apiError?.code) {
                                ApiErrorCode.INVALID_JD_URL -> {
                                    InvalidJdUrlException(
                                        errCode = ApiErrorCode.INVALID_JD_URL,
                                        message = message.ifBlank { "올바른 URL 형식이 아니에요." },
                                        cause = httpError,
                                    )
                                }

                                ApiErrorCode.JD_VALIDATION_LIMIT_EXCEEDED -> {
                                    JdValidationLimitExceededException(
                                        errCode = ApiErrorCode.JD_VALIDATION_LIMIT_EXCEEDED,
                                        message =
                                            message.ifBlank { "공고 링크는 하루에 5번까지만 입력할 수 있어요." },
                                        cause = httpError,
                                    )
                                }

                                else -> {
                                    null
                                }
                            }
                        }
                    }
            return response.toDomain()
        }

        override suspend fun createInterviewSession(
            request: InterviewSessionRequest,
        ): InterviewSessionResult {
            val response =
                runCatching {
                    interviewRemoteDataSource.createInterviewSession(
                        CreateInterviewSessionRequestDto.from(request),
                    )
                }.getOrElse { error ->
                    throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                        mapCreateInterviewSessionValidationError(httpError, apiError)
                            ?: mapCreateInterviewSessionContentError(httpError, apiError)
                    }
                }
            return response.toDomain()
        }

        override suspend fun getInterviewSession(sessionId: Long): InterviewSessionStatus =
            getInterviewSessionStatus(sessionId)

        override suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatus {
            val response =
                runCatching { interviewRemoteDataSource.getInterviewSessionStatus(sessionId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                            if (apiError?.code == ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND) {
                                InterviewSessionNotFoundException(
                                    errCode = ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND,
                                    message =
                                        apiError.message.ifBlank { "면접 세션을 찾을 수 없어요." },
                                    cause = httpError,
                                )
                            } else {
                                null
                            }
                        }
                    }
            return response.toDomain()
        }

        override suspend fun getReportList(): InterviewReportList {
            val response =
                runCatching { interviewRemoteDataSource.getReportList() }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun submitAnswer(
            command: SubmitInterviewAnswerCommand,
        ): SubmitAnswerResult {
            val audioPart =
                command.audioFile?.let { mediaRef ->
                    val file = requireNotNull(interviewLocalRepository).resolveMediaFile(mediaRef)
                    val requestBody = file.asRequestBody("audio/mp4".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData(
                        "audio",
                        "${file.nameWithoutExtension}.m4a",
                        requestBody,
                    )
                }
            val response =
                runCatching {
                    interviewRemoteDataSource.submitAnswer(
                        sessionId = command.sessionId,
                        questionId = command.questionId,
                        isWrapUp = command.isWrapUp,
                        questionAudioStartAt = command.questionAudioStartAt,
                        questionAudioEndAt = command.questionAudioEndAt,
                        answerStartAt = command.answerStartAt,
                        answerEndAt = command.answerEndAt,
                        answerDuration = command.answerDuration,
                        endType = command.endType?.toApiValue(),
                        audio = audioPart,
                    )
                }.getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapInterviewRecoveryError)
                }
            return response.toDomain()
        }

        override fun getAudioStreamUrl(
            sessionId: Long,
            questionId: Long,
        ): String {
            val baseUrl = NetworkConfig.BASE_URL.removeSuffix("/")
            val streamPath =
                "api/v1/interview/sessions/$sessionId/questions/$questionId/audio/stream"
            return "$baseUrl/$streamPath"
        }

        override suspend fun getResume(sessionId: Long): InterviewResumeStatus {
            val response =
                runCatching { interviewRemoteDataSource.getResume(sessionId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirm {
            val response =
                runCatching { interviewRemoteDataSource.confirmResume(sessionId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun abandon(
            sessionId: Long,
            cause: InterviewAbandonRequestCause,
        ): InterviewAbandon {
            val request = InterviewAbandonRequestDto(cause = cause.toApiValue())
            val response =
                runCatching { interviewRemoteDataSource.abandon(sessionId, request) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun getReport(sessionId: Long): InterviewReport {
            val response =
                runCatching { interviewRemoteDataSource.getReport(sessionId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrl {
            val response =
                runCatching { interviewRemoteDataSource.issueUploadUrl(sessionId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun completeUpload(
            sessionId: Long,
            wrapUpStartSec: Float?,
            wrapUpEndSec: Float?,
        ) {
            val request =
                if (wrapUpStartSec != null || wrapUpEndSec != null) {
                    InterviewVideoCompleteRequestDto(
                        wrapUpStartSec = wrapUpStartSec,
                        wrapUpEndSec = wrapUpEndSec,
                    )
                } else {
                    null
                }
            runCatching { interviewRemoteDataSource.completeUpload(sessionId, request) }
                .getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapInterviewRecoveryError)
                }
        }

        override suspend fun getExpiry(sessionId: Long): InterviewVideoExpiry {
            val response =
                runCatching { interviewRemoteDataSource.getExpiry(sessionId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        private fun mapCreateInterviewSessionValidationError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.VALIDATION_ERROR -> {
                    ValidationException(
                        errCode = ApiErrorCode.VALIDATION_ERROR,
                        message = message.ifBlank { "요청 값이 올바르지 않습니다." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.USER_PROFILE_NOT_REGISTERED -> {
                    UserProfileNotRegisteredException(
                        errCode = ApiErrorCode.USER_PROFILE_NOT_REGISTERED,
                        message = message.ifBlank { "면접을 시작하려면 먼저 직무와 연차를 등록해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.JD_URL_AND_TEXT_BOTH_PROVIDED -> {
                    JdUrlAndTextBothProvidedException(
                        errCode = ApiErrorCode.JD_URL_AND_TEXT_BOTH_PROVIDED,
                        message = message.ifBlank { "jdUrl과 jdText는 함께 입력할 수 없어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.JD_NOT_VALIDATED -> {
                    JdNotValidatedException(
                        errCode = ApiErrorCode.JD_NOT_VALIDATED,
                        message = message.ifBlank { "JD 링크를 먼저 검증해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.JD_CONTENT_NOT_FOUND -> {
                    JdContentNotFoundException(
                        errCode = ApiErrorCode.JD_CONTENT_NOT_FOUND,
                        message = message.ifBlank { "JD 링크의 캐시가 만료됐어요. 다시 검증해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.INVALID_JD_LENGTH -> {
                    InvalidJdLengthException(
                        errCode = ApiErrorCode.INVALID_JD_LENGTH,
                        message = message.ifBlank { "JD는 200자 이상 3,000자 이하로 입력해 주세요." },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }

        private fun mapCreateInterviewSessionContentError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.INVALID_FREETEXT_LENGTH -> {
                    InvalidFreeTextLengthException(
                        errCode = ApiErrorCode.INVALID_FREETEXT_LENGTH,
                        message =
                            message.ifBlank { "집중 프로젝트 설명은 10자 이상 300자 이하로 입력해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.FREETEXT_NOT_RELEVANT -> {
                    FreeTextNotRelevantException(
                        errCode = ApiErrorCode.FREETEXT_NOT_RELEVANT,
                        message = message.ifBlank { "입력하신 내용이 포트폴리오와 관련이 적어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.PORTFOLIO_NOT_FOUND -> {
                    PortfolioNotFoundException(
                        errCode = ApiErrorCode.PORTFOLIO_NOT_FOUND,
                        message = message.ifBlank { "포트폴리오를 찾을 수 없어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.PORTFOLIO_PROCESSING -> {
                    PortfolioProcessingException(
                        errCode = ApiErrorCode.PORTFOLIO_PROCESSING,
                        message =
                            message.ifBlank { "포트폴리오를 아직 분석하고 있어요. 잠시 후 다시 시도해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.PORTFOLIO_UPLOAD_FAILED -> {
                    PortfolioUploadFailedException(
                        errCode = ApiErrorCode.PORTFOLIO_UPLOAD_FAILED,
                        message = message.ifBlank { "포트폴리오 처리에 실패했어요. 다시 업로드해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.NO_REMAINING_TICKET -> {
                    NoRemainingTicketException(
                        errCode = ApiErrorCode.NO_REMAINING_TICKET,
                        message = message.ifBlank { "남은 이용권이 없어요." },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }

        override suspend fun uploadVideo(command: UploadInterviewVideoCommand) {
            requireNotNull(interviewVideoUploader).upload(
                uploadUrl = command.uploadUrl,
                contentType = command.contentType,
                file = requireNotNull(interviewLocalRepository).resolveMediaFile(command.mediaRef),
            )
        }

        private fun mapInterviewRecoveryError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val code = apiError?.code ?: return null
            val message = apiError.message
            return when (code) {
                ApiErrorCode.AI_TEMPORARILY_UNAVAILABLE -> {
                    AiTemporarilyUnavailableException(code, message, httpError)
                }

                ApiErrorCode.ANSWER_ALREADY_SUBMITTED -> {
                    AnswerAlreadySubmittedException(code, message, httpError)
                }

                ApiErrorCode.SESSION_ALREADY_ENDED -> {
                    InterviewSessionAlreadyEndedException(code, message, httpError)
                }

                ApiErrorCode.SESSION_NOT_STARTED -> {
                    InterviewSessionNotStartedException(code, message, httpError)
                }

                ApiErrorCode.SESSION_PRELOAD_FAILED -> {
                    InterviewSessionPreloadFailedException(code, message, httpError)
                }

                else -> {
                    null
                }
            }
        }

        private fun InterviewAnswerEndRequest.toApiValue(): String =
            when (this) {
                InterviewAnswerEndRequest.Skip -> "SKIP"
                InterviewAnswerEndRequest.ManualEnd -> "MANUAL_END"
                InterviewAnswerEndRequest.HardCap -> "HARD_CAP"
                InterviewAnswerEndRequest.BackExit -> "BACK_EXIT"
            }

        private fun InterviewAbandonRequestCause.toApiValue(): String =
            when (this) {
                InterviewAbandonRequestCause.NetworkDisconnect -> "NETWORK_DISCONNECT"
                InterviewAbandonRequestCause.UserExit -> "USER_EXIT"
            }
    }

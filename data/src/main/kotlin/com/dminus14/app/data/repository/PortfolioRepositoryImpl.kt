package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.PortfolioRemoteDataSource
import com.dminus14.app.data.remote.dto.ApiErrorResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.DeleteLimitExceededException
import com.dminus14.app.domain.exception.FileTooLargeException
import com.dminus14.app.domain.exception.InvalidFileTypeException
import com.dminus14.app.domain.exception.InvalidPdfFileException
import com.dminus14.app.domain.exception.PageCountExceededException
import com.dminus14.app.domain.exception.PortfolioAlreadyExistsException
import com.dminus14.app.domain.exception.PortfolioDeleteBlockedByInterviewException
import com.dminus14.app.domain.exception.PortfolioNotFoundException
import com.dminus14.app.domain.exception.ReplacementLimitExceededException
import com.dminus14.app.domain.model.PortfolioDeleteResult
import com.dminus14.app.domain.model.PortfolioOverview
import com.dminus14.app.domain.model.PortfolioUploadResult
import com.dminus14.app.domain.repository.PortfolioRepository
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 포트폴리오(업로드·목록·상태·삭제) 원격 DTO를 Domain 계약으로 변환하는 Repository 구현이다.
 *
 * API별 비즈니스 오류는 각 함수에서만 매핑하고, 전송·서버·알 수 없는 오류는
 * 공통 변환 정책([CommonApiErrorMapper])을 사용한다.
 */
@Singleton
class PortfolioRepositoryImpl
    @Inject
    constructor(
        private val portfolioRemoteDataSource: PortfolioRemoteDataSource,
    ) : PortfolioRepository {
        override suspend fun getPortfolioOverview(): PortfolioOverview {
            val response =
                runCatching { portfolioRemoteDataSource.getPortfolios() }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun uploadPortfolio(
            file: File,
            fileName: String,
            fileSize: Long?,
            pageCount: Int?,
            contentType: String,
        ): PortfolioUploadResult {
            val response =
                runCatching {
                    portfolioRemoteDataSource.uploadPortfolio(
                        file = file,
                        fileName = fileName,
                        fileSize = fileSize,
                        pageCount = pageCount,
                        contentType = contentType,
                    )
                }.getOrElse { error ->
                    throw CommonApiErrorMapper.map(error, ::mapUploadPortfolioError)
                }
            return response.toDomain()
        }

        override suspend fun getPortfolioStatus(portfolioId: String): PortfolioUploadResult {
            val response =
                runCatching { portfolioRemoteDataSource.getPortfolioStatus(portfolioId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                            if (apiError?.code == ApiErrorCode.PORTFOLIO_NOT_FOUND) {
                                PortfolioNotFoundException(
                                    errCode = ApiErrorCode.PORTFOLIO_NOT_FOUND,
                                    message = apiError.message.ifBlank { "포트폴리오를 찾을 수 없어요." },
                                    cause = httpError,
                                )
                            } else {
                                null
                            }
                        }
                    }
            return response.toDomain()
        }

        override suspend fun deletePortfolio(portfolioId: String): PortfolioDeleteResult {
            val response =
                runCatching { portfolioRemoteDataSource.deletePortfolio(portfolioId) }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error, ::mapDeletePortfolioError)
                    }
            return response.toDomain()
        }

        private fun mapUploadPortfolioError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.INVALID_FILE_TYPE -> {
                    InvalidFileTypeException(
                        errCode = ApiErrorCode.INVALID_FILE_TYPE,
                        message = message.ifBlank { "PDF 파일만 올릴 수 있어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.FILE_TOO_LARGE -> {
                    FileTooLargeException(
                        errCode = ApiErrorCode.FILE_TOO_LARGE,
                        message = message.ifBlank { "파일이 너무 커요. 20MB 이하 PDF로 올려주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.PAGE_COUNT_EXCEEDED -> {
                    PageCountExceededException(
                        errCode = ApiErrorCode.PAGE_COUNT_EXCEEDED,
                        message = message.ifBlank { "페이지가 너무 많아요. 30페이지 이하 PDF로 올려주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.INVALID_PDF_FILE -> {
                    InvalidPdfFileException(
                        errCode = ApiErrorCode.INVALID_PDF_FILE,
                        message =
                            message.ifBlank { "파일이 손상된 것 같아요. 파일을 확인하고 다시 시도해 주세요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.PORTFOLIO_ALREADY_EXISTS -> {
                    PortfolioAlreadyExistsException(
                        errCode = ApiErrorCode.PORTFOLIO_ALREADY_EXISTS,
                        message =
                            message.ifBlank {
                                "이미 등록된 포트폴리오가 있어요. 기존 포트폴리오를 삭제한 뒤 새로 올려주세요."
                            },
                        cause = httpError,
                    )
                }

                ApiErrorCode.REPLACEMENT_LIMIT_EXCEEDED -> {
                    ReplacementLimitExceededException(
                        errCode = ApiErrorCode.REPLACEMENT_LIMIT_EXCEEDED,
                        message =
                            message.ifBlank {
                                "포트폴리오 재업로드는 한 달에 한 번만 가능해요. 다음 달 1일부터 다시 시도해 주세요."
                            },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }

        private fun mapDeletePortfolioError(
            httpError: HttpException,
            apiError: ApiErrorResponseDto?,
        ): CustomException? {
            val message = apiError?.message.orEmpty()
            return when (apiError?.code) {
                ApiErrorCode.PORTFOLIO_NOT_FOUND -> {
                    PortfolioNotFoundException(
                        errCode = ApiErrorCode.PORTFOLIO_NOT_FOUND,
                        message = message.ifBlank { "포트폴리오를 찾을 수 없어요." },
                        cause = httpError,
                    )
                }

                ApiErrorCode.PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW -> {
                    PortfolioDeleteBlockedByInterviewException(
                        errCode = ApiErrorCode.PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW,
                        message =
                            message.ifBlank {
                                "진행 중인 면접이 있어 포트폴리오를 삭제할 수 없어요. " +
                                    "면접 종료 후 다시 시도해 주세요."
                            },
                        cause = httpError,
                    )
                }

                ApiErrorCode.DELETE_LIMIT_EXCEEDED -> {
                    DeleteLimitExceededException(
                        errCode = ApiErrorCode.DELETE_LIMIT_EXCEEDED,
                        message =
                            message.ifBlank {
                                "포트폴리오 삭제는 한 달에 한 번만 가능해요. " +
                                    "다음 달 1일부터 다시 시도해 주세요."
                            },
                        cause = httpError,
                    )
                }

                else -> {
                    null
                }
            }
        }
    }

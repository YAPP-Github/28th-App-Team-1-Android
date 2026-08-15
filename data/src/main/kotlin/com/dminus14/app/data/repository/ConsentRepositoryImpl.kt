package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.ConsentRemoteDataSource
import com.dminus14.app.data.remote.dto.consent.ConsentSubmitRequestDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.ConsentDocumentNotFoundException
import com.dminus14.app.domain.exception.ConsentVersionMismatchException
import com.dminus14.app.domain.exception.InvalidConsentItemException
import com.dminus14.app.domain.exception.RequiredConsentMissingException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.PendingConsentList
import com.dminus14.app.domain.repository.ConsentRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Consent 원격 DTO를 Domain 계약으로 변환하는 Repository 구현이다.
 *
 * 동의 문서 본문은 저장하거나 로그로 남기지 않는다. API별 비즈니스 오류는 각 함수에서만
 * 매핑하고, 전송·서버·알 수 없는 오류는 공통 변환 정책을 사용한다.
 */
@Singleton
class ConsentRepositoryImpl
    @Inject
    constructor(
        private val consentRemoteDataSource: ConsentRemoteDataSource,
    ) : ConsentRepository {
        override suspend fun getPendingConsentList(): PendingConsentList {
            val response =
                runCatching { consentRemoteDataSource.getPendingConsentList() }
                    .getOrElse { error ->
                        throw CommonApiErrorMapper.map(error)
                    }
            return response.toDomain()
        }

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocument {
            val response =
                runCatching {
                    consentRemoteDataSource.getConsentDocument(
                        rawCode = rawCode,
                        version = version,
                    )
                }.getOrElse { error ->
                    throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                        val message = apiError?.message.orEmpty()
                        when (apiError?.code) {
                            ApiErrorCode.INVALID_CONSENT_ITEM -> {
                                InvalidConsentItemException(
                                    errCode = ApiErrorCode.INVALID_CONSENT_ITEM,
                                    message = message.ifBlank { "지원하지 않는 동의 항목이에요." },
                                    cause = httpError,
                                )
                            }

                            ApiErrorCode.DOCUMENT_NOT_FOUND -> {
                                ConsentDocumentNotFoundException(
                                    errCode = ApiErrorCode.DOCUMENT_NOT_FOUND,
                                    message = message.ifBlank { "존재하지 않는 동의 문서예요." },
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

        override suspend fun submitConsent(submission: ConsentSubmission) {
            runCatching {
                consentRemoteDataSource.submitConsent(ConsentSubmitRequestDto.from(submission))
            }.getOrElse { error ->
                throw CommonApiErrorMapper.map(error) { httpError, apiError ->
                    val message = apiError?.message.orEmpty()
                    when (apiError?.code) {
                        ApiErrorCode.VALIDATION_ERROR -> {
                            ValidationException(
                                errCode = ApiErrorCode.VALIDATION_ERROR,
                                message = message.ifBlank { "요청 값이 올바르지 않습니다." },
                                cause = httpError,
                            )
                        }

                        ApiErrorCode.INVALID_CONSENT_ITEM -> {
                            InvalidConsentItemException(
                                errCode = ApiErrorCode.INVALID_CONSENT_ITEM,
                                message = message.ifBlank { "지원하지 않는 동의 항목이에요." },
                                cause = httpError,
                            )
                        }

                        ApiErrorCode.REQUIRED_CONSENT_MISSING -> {
                            RequiredConsentMissingException(
                                errCode = ApiErrorCode.REQUIRED_CONSENT_MISSING,
                                message = message.ifBlank { "필수 동의 항목이 누락됐어요." },
                                cause = httpError,
                            )
                        }

                        ApiErrorCode.CONSENT_VERSION_MISMATCH -> {
                            ConsentVersionMismatchException(
                                errCode = ApiErrorCode.CONSENT_VERSION_MISMATCH,
                                message =
                                    message.ifBlank {
                                        "동의 항목 버전이 최신이 아니에요. 목록을 다시 조회해주세요."
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
        }
    }

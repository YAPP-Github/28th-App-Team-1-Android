package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.ConsentRemoteDataSource
import com.dminus14.app.data.remote.dto.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.ConsentItemDto
import com.dminus14.app.data.remote.dto.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.ConsentSubmitRequestDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ConsentDocumentNotFoundException
import com.dminus14.app.domain.exception.ConsentVersionMismatchException
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.InvalidConsentItemException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.RequiredConsentMissingException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.ConsentItemCode
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.ConsentSubmissionItem
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ConsentRepositoryImplTest {
    @Test
    fun `pending 목록 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeConsentRemoteDataSource()
        val repository = ConsentRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getPendingConsentList() }

        assertEquals(ConsentPendingStatus.STALE, actual.status)
        assertEquals(ConsentItemCode.TERMS_OF_SERVICE, actual.items.single().code)
        assertEquals(1, dataSource.getPendingCallCount)
    }

    @Test
    fun `문서 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeConsentRemoteDataSource()
        val repository = ConsentRepositoryImpl(dataSource)

        val actual =
            runBlocking {
                repository.getConsentDocument(
                    rawCode = "TERMS_OF_SERVICE",
                    version = 2,
                )
            }

        assertEquals("TERMS_OF_SERVICE", actual.rawCode)
        assertEquals(2, actual.version)
        assertEquals("합성 예시 본문", actual.contentMarkdown)
        assertEquals("TERMS_OF_SERVICE", dataSource.requestedDocumentCode)
        assertEquals(2, dataSource.requestedDocumentVersion)
    }

    @Test
    fun `제출 모델을 DTO로 변환해 저장소에 전달한다`() {
        val dataSource = FakeConsentRemoteDataSource()
        val repository = ConsentRepositoryImpl(dataSource)
        val submission =
            ConsentSubmission(
                items =
                    listOf(
                        ConsentSubmissionItem(
                            rawCode = "AGE_OVER_14",
                            version = 1,
                            agreed = true,
                        ),
                    ),
            )

        runBlocking { repository.submitConsent(submission) }

        assertEquals(1, dataSource.submitCallCount)
        assertEquals(
            "AGE_OVER_14",
            dataSource.submittedRequest
                ?.items
                ?.single()
                ?.item,
        )
        assertTrue(
            dataSource.submittedRequest
                ?.items
                ?.single()
                ?.agreed == true,
        )
    }

    @Test
    fun `문서 조회 비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.INVALID_CONSENT_ITEM to InvalidConsentItemException::class.java,
                ApiErrorCode.DOCUMENT_NOT_FOUND to ConsentDocumentNotFoundException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(400, code)
            val repository =
                ConsentRepositoryImpl(
                    FakeConsentRemoteDataSource(failure = httpError),
                )

            val actual =
                captureFailure {
                    repository.getConsentDocument(rawCode = "TERMS_OF_SERVICE", version = 1)
                }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `제출 비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.VALIDATION_ERROR to ValidationException::class.java,
                ApiErrorCode.INVALID_CONSENT_ITEM to InvalidConsentItemException::class.java,
                ApiErrorCode.REQUIRED_CONSENT_MISSING to
                    RequiredConsentMissingException::class.java,
                ApiErrorCode.CONSENT_VERSION_MISMATCH to
                    ConsentVersionMismatchException::class.java,
            )
        val submission =
            ConsentSubmission(
                items =
                    listOf(
                        ConsentSubmissionItem(
                            rawCode = "AGE_OVER_14",
                            version = 1,
                            agreed = true,
                        ),
                    ),
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(400, code)
            val repository =
                ConsentRepositoryImpl(
                    FakeConsentRemoteDataSource(failure = httpError),
                )

            val actual = captureFailure { repository.submitConsent(submission) }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `공통 네트워크 서버 알 수 없는 오류 정책을 유지한다`() {
        val cases =
            listOf(
                IOException("synthetic offline") to NetworkUnavailableException::class.java,
                httpException(500, "SYNTHETIC_SERVER_ERROR") to ServerException::class.java,
                IllegalStateException("synthetic invalid state") to UnknownException::class.java,
            )

        cases.forEach { (failure, expectedType) ->
            val repository =
                ConsentRepositoryImpl(
                    FakeConsentRemoteDataSource(failure = failure),
                )

            val actual = captureFailure { repository.getPendingConsentList() }

            assertTrue(expectedType.isInstance(actual))
            assertSame(failure, actual.cause)
        }
    }

    private fun captureFailure(block: suspend () -> Unit): Throwable {
        try {
            runBlocking { block() }
        } catch (error: Throwable) {
            return error
        }
        throw AssertionError("예외가 발생해야 합니다.")
    }

    private fun httpException(
        status: Int,
        code: String,
    ): HttpException {
        val body =
            """
            {"success":false,"code":"$code","message":"synthetic $code"}
            """.trimIndent()
                .toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(status, body))
    }

    private class FakeConsentRemoteDataSource(
        private val failure: Throwable? = null,
        private val pending: ConsentPendingItemsDto =
            ConsentPendingItemsDto(
                status = "STALE",
                items =
                    listOf(
                        ConsentItemDto(
                            code = "TERMS_OF_SERVICE",
                            label = "서비스 이용약관",
                            required = true,
                            version = 2,
                            hasDocument = true,
                        ),
                    ),
            ),
        private val document: ConsentDocumentDto =
            ConsentDocumentDto(
                item = "TERMS_OF_SERVICE",
                title = "서비스 이용약관",
                version = 2,
                content = "합성 예시 본문",
            ),
    ) : ConsentRemoteDataSource {
        var getPendingCallCount = 0
            private set
        var requestedDocumentCode: String? = null
            private set
        var requestedDocumentVersion: Int? = null
            private set
        var submitCallCount = 0
            private set
        var submittedRequest: ConsentSubmitRequestDto? = null
            private set

        override suspend fun getPendingConsentList(): ConsentPendingItemsDto {
            failure?.let { throw it }
            getPendingCallCount += 1
            return pending
        }

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocumentDto {
            failure?.let { throw it }
            requestedDocumentCode = rawCode
            requestedDocumentVersion = version
            return document
        }

        override suspend fun submitConsent(request: ConsentSubmitRequestDto) {
            failure?.let { throw it }
            submitCallCount += 1
            submittedRequest = request
        }
    }
}

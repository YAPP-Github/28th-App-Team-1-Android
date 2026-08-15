package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.ConsentApi
import com.dminus14.app.data.remote.dto.common.ApiResponseDto
import com.dminus14.app.data.remote.dto.consent.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.consent.ConsentItemSubmissionDto
import com.dminus14.app.data.remote.dto.consent.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.consent.ConsentSubmitRequestDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ConsentRemoteDataSourceTest {
    @Test
    fun `pending 목록 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeConsentApi(
                pendingResponse = ApiResponseDto(success = true, data = null),
            )
        val dataSource = ConsentRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getPendingConsentList() }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("동의 pending 목록 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `동의 문서 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeConsentApi(
                documentResponse = ApiResponseDto(success = true, data = null),
            )
        val dataSource = ConsentRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking {
                    dataSource.getConsentDocument(rawCode = "TERMS_OF_SERVICE", version = 1)
                }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("동의 문서 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `동의 제출 성공 응답이면 예외 없이 완료한다`() {
        val request = sampleSubmitRequest()
        val api =
            FakeConsentApi(
                submitResponse = ApiResponseDto(success = true, data = null),
            )
        val dataSource = ConsentRemoteDataSourceImpl(api)

        runBlocking { dataSource.submitConsent(request) }

        assertEquals(request, api.submittedRequest)
    }

    @Test
    fun `동의 제출 응답 success가 false면 ServerException을 던진다`() {
        val api =
            FakeConsentApi(
                submitResponse = ApiResponseDto(success = false, data = null),
            )
        val dataSource = ConsentRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.submitConsent(sampleSubmitRequest()) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("동의 제출에 실패했습니다.", actual.message)
    }

    private class FakeConsentApi(
        private val pendingResponse: ApiResponseDto<ConsentPendingItemsDto> =
            ApiResponseDto(
                success = true,
                data =
                    ConsentPendingItemsDto(
                        status = "UP_TO_DATE",
                        items = emptyList(),
                    ),
            ),
        private val documentResponse: ApiResponseDto<ConsentDocumentDto> =
            ApiResponseDto(
                success = true,
                data =
                    ConsentDocumentDto(
                        item = "TERMS_OF_SERVICE",
                        title = "서비스 이용약관",
                        version = 1,
                        content = "synthetic",
                    ),
            ),
        private val submitResponse: ApiResponseDto<Unit?> =
            ApiResponseDto(success = true, data = null),
    ) : ConsentApi {
        var submittedRequest: ConsentSubmitRequestDto? = null
            private set

        override suspend fun getPendingConsentList(): ApiResponseDto<ConsentPendingItemsDto> =
            pendingResponse

        override suspend fun getConsentDocument(
            item: String,
            version: Int,
        ): ApiResponseDto<ConsentDocumentDto> = documentResponse

        override suspend fun submitConsent(
            request: ConsentSubmitRequestDto,
        ): ApiResponseDto<Unit?> {
            submittedRequest = request
            return submitResponse
        }
    }

    private companion object {
        fun sampleSubmitRequest(): ConsentSubmitRequestDto =
            ConsentSubmitRequestDto(
                items =
                    listOf(
                        ConsentItemSubmissionDto(
                            item = "TERMS_OF_SERVICE",
                            version = 1,
                            agreed = true,
                        ),
                    ),
            )
    }
}

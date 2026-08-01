package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.ConsentApi
import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.ConsentItemSubmissionDto
import com.dminus14.app.data.remote.dto.ConsentSubmitRequestDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ConsentRemoteDataSourceTest {
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
    fun `동의 제출 응답 success가 false면 예외를 던진다`() {
        val api =
            FakeConsentApi(
                submitResponse = ApiResponseDto(success = false, data = null),
            )
        val dataSource = ConsentRemoteDataSourceImpl(api)

        val actual =
            assertThrows(IllegalStateException::class.java) {
                runBlocking { dataSource.submitConsent(sampleSubmitRequest()) }
            }

        assertEquals("동의 제출에 실패했습니다.", actual.message)
    }

    private class FakeConsentApi(
        private val submitResponse: ApiResponseDto<Unit?> =
            ApiResponseDto(success = true, data = null),
    ) : ConsentApi {
        var submittedRequest: ConsentSubmitRequestDto? = null
            private set

        override suspend fun getPendingConsentList(): ApiResponseDto<ConsentPendingItemsDto> =
            error("Not used in ConsentRemoteDataSourceTest")

        override suspend fun getConsentDocument(
            item: String,
            version: Int,
        ): ApiResponseDto<ConsentDocumentDto> = error("Not used in ConsentRemoteDataSourceTest")

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

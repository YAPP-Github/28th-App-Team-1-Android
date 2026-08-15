package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.GuestFeedbackApi
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackGateDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitRequestDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitResponseDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException
import java.time.Instant

/** Guest Feedback 원격 데이터 소스가 변환 없이 API 호출과 요청 조립만 담당하는지 검증한다. */
class GuestFeedbackRemoteDataSourceTest {
    @Test
    fun `진입 호출을 위임하고 검증된 직접 응답을 반환한다`() {
        val response = closedResponse()
        val api = FakeGuestFeedbackApi(entryResponse = response)
        val dataSource = createDataSource(api)

        val actual = runBlocking { dataSource.enter(SYNTHETIC_TOKEN) }

        assertSame(response, actual)
        assertEquals(SYNTHETIC_TOKEN, api.enteredToken)
    }

    @Test
    fun `제출 DTO에 nullable 값을 포함해 조립하고 직접 응답을 반환한다`() {
        val response = validSubmitResponse()
        val api = FakeGuestFeedbackApi(submitResponse = response)
        val dataSource = createDataSource(api)
        val ratings =
            listOf(
                GuestFeedbackRatingDto(
                    axis = GuestFeedbackAxisCodeDto.EXPRESSION,
                    level = 3,
                    comment = null,
                ),
            )

        val actual =
            runBlocking {
                dataSource.submit(
                    token = SYNTHETIC_TOKEN,
                    nickname = null,
                    ratings = ratings,
                )
            }

        assertSame(response, actual)
        assertEquals(SYNTHETIC_TOKEN, api.submittedToken)
        assertNull(api.submittedRequest?.nickname)
        assertEquals(ratings, api.submittedRequest?.ratings)
    }

    @Test
    fun `API 예외를 변환하지 않고 그대로 전파한다`() {
        val failure = IOException("synthetic transport failure")
        val dataSource = createDataSource(FakeGuestFeedbackApi(failure = failure))

        val actual =
            assertThrows(IOException::class.java) {
                runBlocking { dataSource.enter(SYNTHETIC_TOKEN) }
            }

        assertSame(failure, actual)
    }

    /** 테스트 API만 주입해 별도 응답 검증 의존성이 제거됐는지 함께 확인한다. */
    private fun createDataSource(api: GuestFeedbackApi): GuestFeedbackRemoteDataSource =
        GuestFeedbackRemoteDataSourceImpl(api)

    private class FakeGuestFeedbackApi(
        private val entryResponse: GuestFeedbackEntryResponseDto = closedResponse(),
        private val submitResponse: GuestFeedbackSubmitResponseDto = validSubmitResponse(),
        private val failure: Exception? = null,
    ) : GuestFeedbackApi {
        var enteredToken: String? = null
            private set
        var submittedToken: String? = null
            private set
        var submittedRequest: GuestFeedbackSubmitRequestDto? = null
            private set

        override suspend fun enter(token: String): GuestFeedbackEntryResponseDto {
            failure?.let { throw it }
            enteredToken = token
            return entryResponse
        }

        override suspend fun submit(
            token: String,
            request: GuestFeedbackSubmitRequestDto,
        ): GuestFeedbackSubmitResponseDto {
            failure?.let { throw it }
            submittedToken = token
            submittedRequest = request
            return submitResponse
        }
    }

    private companion object {
        const val SYNTHETIC_TOKEN = "synthetic-token"

        fun closedResponse() =
            GuestFeedbackEntryResponseDto(
                gate = GuestFeedbackGateDto.ALREADY_SUBMITTED,
                requesterName = null,
                axes = null,
                videoUrl = null,
                questionBoundaries = null,
                submissionOpen = null,
            )

        fun validSubmitResponse() =
            GuestFeedbackSubmitResponseDto(
                submissionId = 41L,
                submittedAt = Instant.parse("2026-07-30T09:58:13.348Z"),
            )
    }
}

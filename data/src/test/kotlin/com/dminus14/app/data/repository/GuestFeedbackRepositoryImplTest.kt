package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.GuestFeedbackRemoteDataSource
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackGateDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.GuestFeedbackAlreadySubmittedException
import com.dminus14.app.domain.exception.GuestFeedbackCapacityFullException
import com.dminus14.app.domain.exception.GuestFeedbackRequestException
import com.dminus14.app.domain.exception.GuestFeedbackShareClosedException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackRating
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.model.GuestFeedbackUnavailableReason
import com.google.gson.JsonParseException
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
import java.time.Instant

class GuestFeedbackRepositoryImplTest {
    @Test
    fun `진입 호출을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeGuestFeedbackRemoteDataSource()
        val repository = GuestFeedbackRepositoryImpl(dataSource)

        val actual = runBlocking { repository.enter(SYNTHETIC_TOKEN) }

        assertEquals(
            GuestFeedbackEntry.Unavailable(GuestFeedbackUnavailableReason.FULL),
            actual,
        )
        assertEquals(SYNTHETIC_TOKEN, dataSource.enteredToken)
    }

    @Test
    fun `제출 모델을 DTO로 변환하고 성공 응답 값은 노출하지 않는다`() {
        val dataSource = FakeGuestFeedbackRemoteDataSource()
        val repository = GuestFeedbackRepositoryImpl(dataSource)
        val submission =
            GuestFeedbackSubmission(
                nickname = "합성 지인",
                ratings =
                    listOf(
                        GuestFeedbackRating(
                            axis = GuestFeedbackAxisCode.EXPRESSION,
                            level = 2,
                            comment = "합성 코멘트",
                        ),
                    ),
            )

        val actual = runBlocking { repository.submit(SYNTHETIC_TOKEN, submission) }

        assertSame(Unit, actual)
        assertEquals(SYNTHETIC_TOKEN, dataSource.submittedToken)
        assertEquals("합성 지인", dataSource.submittedNickname)
        assertEquals(
            listOf(
                GuestFeedbackRatingDto(
                    axis = GuestFeedbackAxisCodeDto.EXPRESSION,
                    level = 2,
                    comment = "합성 코멘트",
                ),
            ),
            dataSource.submittedRatings,
        )
    }

    @Test
    fun `일곱 비즈니스 오류 코드를 네 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.FEEDBACK_SHARE_TOKEN_NOT_FOUND to
                    GuestFeedbackRequestException::class.java,
                ApiErrorCode.INCOMPLETE_RATINGS to GuestFeedbackRequestException::class.java,
                ApiErrorCode.INVALID_RATING_LEVEL to GuestFeedbackRequestException::class.java,
                ApiErrorCode.MISSING_DEVICE_ID to GuestFeedbackRequestException::class.java,
                ApiErrorCode.FEEDBACK_SHARE_CLOSED to GuestFeedbackShareClosedException::class.java,
                ApiErrorCode.FEEDBACK_CAPACITY_FULL to
                    GuestFeedbackCapacityFullException::class.java,
                ApiErrorCode.FEEDBACK_ALREADY_SUBMITTED to
                    GuestFeedbackAlreadySubmittedException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(409, code)
            val repository =
                GuestFeedbackRepositoryImpl(
                    FakeGuestFeedbackRemoteDataSource(failure = httpError),
                )

            val actual = captureFailure { repository.enter(SYNTHETIC_TOKEN) }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(
                code,
                (actual as com.dminus14.app.domain.exception.CustomException).errCode,
            )
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
                JsonParseException("synthetic invalid response") to UnknownException::class.java,
            )

        cases.forEach { (failure, expectedType) ->
            val repository =
                GuestFeedbackRepositoryImpl(
                    FakeGuestFeedbackRemoteDataSource(failure = failure),
                )

            val actual = captureFailure { repository.enter(SYNTHETIC_TOKEN) }

            assertTrue(expectedType.isInstance(actual))
            assertSame(failure, actual.cause)
        }
    }

    private fun captureFailure(block: suspend () -> Unit): Throwable =
        try {
            runBlocking { block() }
            throw AssertionError("예외가 발생해야 합니다.")
        } catch (error: Throwable) {
            error
        }

    private fun httpException(
        status: Int,
        code: String,
    ): HttpException {
        val body =
            """{"success":false,"code":"$code","message":"합성 오류"}"""
                .toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(status, body))
    }

    private class FakeGuestFeedbackRemoteDataSource(
        private val entryResponse: GuestFeedbackEntryResponseDto = closedResponse(),
        private val submitResponse: GuestFeedbackSubmitResponseDto = submitResponse(),
        private val failure: Throwable? = null,
    ) : GuestFeedbackRemoteDataSource {
        var enteredToken: String? = null
            private set
        var submittedToken: String? = null
            private set
        var submittedNickname: String? = null
            private set
        var submittedRatings: List<GuestFeedbackRatingDto>? = null
            private set

        override suspend fun enter(token: String): GuestFeedbackEntryResponseDto {
            failure?.let { throw it }
            enteredToken = token
            return entryResponse
        }

        override suspend fun submit(
            token: String,
            nickname: String?,
            ratings: List<GuestFeedbackRatingDto>,
        ): GuestFeedbackSubmitResponseDto {
            failure?.let { throw it }
            submittedToken = token
            submittedNickname = nickname
            submittedRatings = ratings
            return submitResponse
        }
    }

    private companion object {
        const val SYNTHETIC_TOKEN = "synthetic-token"

        fun closedResponse() =
            GuestFeedbackEntryResponseDto(
                gate = GuestFeedbackGateDto.FULL,
                requesterName = null,
                axes = null,
                videoUrl = null,
                submissionOpen = null,
            )

        fun submitResponse() =
            GuestFeedbackSubmitResponseDto(
                submissionId = 41L,
                submittedAt = Instant.parse("2026-07-30T09:58:13.348Z"),
            )
    }
}

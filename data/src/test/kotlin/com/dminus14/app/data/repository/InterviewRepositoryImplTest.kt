package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.dto.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.JdValidateResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidJdUrlException
import com.dminus14.app.domain.exception.JdUrlAndTextBothProvidedException
import com.dminus14.app.domain.exception.JdValidationLimitExceededException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.NoRemainingTicketException
import com.dminus14.app.domain.exception.PortfolioNotFoundException
import com.dminus14.app.domain.exception.PortfolioProcessingException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionStatusType
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

class InterviewRepositoryImplTest {
    @Test
    fun `JD 검증을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.validateJdUrl("https://example.com/jd") }

        assertTrue(actual.valid)
        assertEquals("https://example.com/jd", dataSource.requestedJdUrl)
    }

    @Test
    fun `세션 생성을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.createInterviewSession(sampleRequest()) }

        assertEquals(42L, actual.sessionId)
        assertEquals(InterviewSessionStatusType.PROCESSING, actual.status)
        assertEquals("portfolio-1", dataSource.requestedCreateSession?.portfolioId)
    }

    @Test
    fun `세션 상태 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getInterviewSessionStatus(42L) }

        assertEquals(InterviewSessionStatusType.READY, actual.status)
        assertEquals(42L, dataSource.requestedSessionId)
    }

    @Test
    fun `JD 검증 비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.INVALID_JD_URL to InvalidJdUrlException::class.java,
                ApiErrorCode.JD_VALIDATION_LIMIT_EXCEEDED to
                    JdValidationLimitExceededException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(400, code)
            val repository =
                InterviewRepositoryImpl(FakeInterviewRemoteDataSource(failure = httpError))

            val actual = captureFailure { repository.validateJdUrl("https://example.com/jd") }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `세션 생성 비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.VALIDATION_ERROR to ValidationException::class.java,
                ApiErrorCode.JD_URL_AND_TEXT_BOTH_PROVIDED to
                    JdUrlAndTextBothProvidedException::class.java,
                ApiErrorCode.PORTFOLIO_NOT_FOUND to PortfolioNotFoundException::class.java,
                ApiErrorCode.PORTFOLIO_PROCESSING to PortfolioProcessingException::class.java,
                ApiErrorCode.NO_REMAINING_TICKET to NoRemainingTicketException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(400, code)
            val repository =
                InterviewRepositoryImpl(FakeInterviewRemoteDataSource(failure = httpError))

            val actual = captureFailure { repository.createInterviewSession(sampleRequest()) }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `세션 상태 조회 not found 오류를 도메인 오류로 변환한다`() {
        val httpError = httpException(404, ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND)
        val repository = InterviewRepositoryImpl(FakeInterviewRemoteDataSource(failure = httpError))

        val actual = captureFailure { repository.getInterviewSessionStatus(42L) }

        assertTrue(actual is InterviewSessionNotFoundException)
        assertEquals(ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND, (actual as CustomException).errCode)
        assertSame(httpError, actual.cause)
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
                InterviewRepositoryImpl(FakeInterviewRemoteDataSource(failure = failure))

            val actual = captureFailure { repository.validateJdUrl("https://example.com/jd") }

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

    private class FakeInterviewRemoteDataSource(
        private val failure: Throwable? = null,
    ) : InterviewRemoteDataSource {
        var requestedJdUrl: String? = null
            private set
        var requestedCreateSession: CreateInterviewSessionRequestDto? = null
            private set
        var requestedSessionId: Long? = null
            private set

        override suspend fun validateJdUrl(jdUrl: String): JdValidateResponseDto {
            failure?.let { throw it }
            requestedJdUrl = jdUrl
            return JdValidateResponseDto(valid = true, reason = null, message = null)
        }

        override suspend fun createInterviewSession(
            request: CreateInterviewSessionRequestDto,
        ): InterviewSessionResponseDto {
            failure?.let { throw it }
            requestedCreateSession = request
            return InterviewSessionResponseDto(
                sessionId = 42L,
                status = "PROCESSING",
                statusUrl = "api/v1/interview/sessions/42/status",
            )
        }

        override suspend fun getInterviewSessionStatus(
            sessionId: Long,
        ): InterviewSessionStatusResponseDto {
            failure?.let { throw it }
            requestedSessionId = sessionId
            return InterviewSessionStatusResponseDto(
                status = "READY",
                startedAt = "2026-08-04T00:00:00Z",
                summaryQuestion = null,
            )
        }
    }

    private companion object {
        fun sampleRequest(): InterviewSessionRequest =
            InterviewSessionRequest(
                portfolioId = "portfolio-1",
                jobRole = "BACKEND",
                careerYears = 3,
                jdUrl = "https://example.com/jd",
            )
    }
}

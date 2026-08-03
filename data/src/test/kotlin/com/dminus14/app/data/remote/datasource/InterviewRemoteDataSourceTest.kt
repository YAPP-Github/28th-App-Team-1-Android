package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.InterviewApi
import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.JdValidateRequestDto
import com.dminus14.app.data.remote.dto.JdValidateResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class InterviewRemoteDataSourceTest {
    @Test
    fun `JD 검증 응답을 그대로 반환한다`() {
        val expected = JdValidateResponseDto(valid = true, reason = null, message = null)
        val api =
            FakeInterviewApi(jdValidateResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.validateJdUrl("https://example.com/jd") }

        assertSame(expected, actual)
        assertEquals("https://example.com/jd", api.requestedJdUrl)
    }

    @Test
    fun `JD 검증 data가 null이면 ServerException을 던진다`() {
        val api = FakeInterviewApi(jdValidateResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.validateJdUrl("https://example.com/jd") }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("JD 유효성 검사 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `세션 생성 응답을 그대로 반환한다`() {
        val expected =
            InterviewSessionResponseDto(
                sessionId = 42L,
                status = "PROCESSING",
                statusUrl = "api/v1/interview/sessions/42/status",
            )
        val api =
            FakeInterviewApi(
                createSessionResponse = ApiResponseDto(success = true, data = expected),
            )
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.createInterviewSession(sampleRequest()) }

        assertSame(expected, actual)
        assertEquals(sampleRequest(), api.requestedCreateSession)
    }

    @Test
    fun `세션 생성 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeInterviewApi(createSessionResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.createInterviewSession(sampleRequest()) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 세션 생성 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `세션 상태 응답을 그대로 반환한다`() {
        val expected =
            InterviewSessionStatusResponseDto(
                status = "READY",
                startedAt = "2026-08-04T00:00:00Z",
                summaryQuestion = null,
            )
        val api =
            FakeInterviewApi(
                sessionStatusResponse = ApiResponseDto(success = true, data = expected),
            )
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.getInterviewSessionStatus(42L) }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedSessionId)
    }

    @Test
    fun `세션 상태 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeInterviewApi(sessionStatusResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getInterviewSessionStatus(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 세션 상태 응답이 비어 있습니다.", actual.message)
    }

    private class FakeInterviewApi(
        private val jdValidateResponse: ApiResponseDto<JdValidateResponseDto> =
            ApiResponseDto(
                success = true,
                data = JdValidateResponseDto(valid = true, reason = null, message = null),
            ),
        private val createSessionResponse: ApiResponseDto<InterviewSessionResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewSessionResponseDto(
                        sessionId = 1L,
                        status = "PROCESSING",
                        statusUrl = "api/v1/interview/sessions/1/status",
                    ),
            ),
        private val sessionStatusResponse: ApiResponseDto<InterviewSessionStatusResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewSessionStatusResponseDto(
                        status = "PROCESSING",
                        startedAt = null,
                        summaryQuestion = null,
                    ),
            ),
    ) : InterviewApi {
        var requestedJdUrl: String? = null
            private set
        var requestedCreateSession: CreateInterviewSessionRequestDto? = null
            private set
        var requestedSessionId: Long? = null
            private set

        override suspend fun validateJdUrl(
            request: JdValidateRequestDto,
        ): ApiResponseDto<JdValidateResponseDto> {
            requestedJdUrl = request.jdUrl
            return jdValidateResponse
        }

        override suspend fun createInterviewSession(
            request: CreateInterviewSessionRequestDto,
        ): ApiResponseDto<InterviewSessionResponseDto> {
            requestedCreateSession = request
            return createSessionResponse
        }

        override suspend fun getInterviewSessionStatus(
            sessionId: Long,
        ): ApiResponseDto<InterviewSessionStatusResponseDto> {
            requestedSessionId = sessionId
            return sessionStatusResponse
        }
    }

    private companion object {
        fun sampleRequest(): CreateInterviewSessionRequestDto =
            CreateInterviewSessionRequestDto(
                portfolioId = "portfolio-1",
                jobRole = "BACKEND",
                careerYears = 3,
                jdUrl = "https://example.com/jd",
            )
    }
}

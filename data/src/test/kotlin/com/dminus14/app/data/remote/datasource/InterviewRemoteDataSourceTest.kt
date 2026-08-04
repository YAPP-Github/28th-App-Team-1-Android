package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.InterviewApi
import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.InterviewAbandonRequestDto
import com.dminus14.app.data.remote.dto.InterviewAbandonResponseDto
import com.dminus14.app.data.remote.dto.InterviewReportListResponseDto
import com.dminus14.app.data.remote.dto.InterviewReportResponseDto
import com.dminus14.app.data.remote.dto.InterviewResumeConfirmResponseDto
import com.dminus14.app.data.remote.dto.InterviewResumeStatusResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.InterviewVideoCompleteRequestDto
import com.dminus14.app.data.remote.dto.InterviewVideoExpiryResponseDto
import com.dminus14.app.data.remote.dto.InterviewVideoUploadUrlResponseDto
import com.dminus14.app.data.remote.dto.JdValidateRequestDto
import com.dminus14.app.data.remote.dto.JdValidateResponseDto
import com.dminus14.app.data.remote.dto.SubmitAnswerResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

    @Test
    fun `면접 레포트 목록 응답을 그대로 반환한다`() {
        val expected = InterviewReportListResponseDto(reports = emptyList())
        val api =
            FakeInterviewApi(reportListResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.getReportList() }

        assertSame(expected, actual)
    }

    @Test
    fun `면접 레포트 목록 data가 null이면 ServerException을 던진다`() {
        val api = FakeInterviewApi(reportListResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getReportList() }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 레포트 목록 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `답변 제출 응답을 그대로 반환한다`() {
        val expected = SubmitAnswerResponseDto(sessionEnded = false)
        val api =
            FakeInterviewApi(submitAnswerResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            runBlocking {
                dataSource.submitAnswer(
                    sessionId = 42L,
                    questionId = 1L,
                    isWrapUp = false,
                )
            }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedSubmitSessionId)
        assertEquals(1L, api.requestedSubmitQuestionId)
    }

    @Test
    fun `답변 제출 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeInterviewApi(submitAnswerResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking {
                    dataSource.submitAnswer(
                        sessionId = 42L,
                        questionId = 1L,
                        isWrapUp = false,
                    )
                }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("답변 제출 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `음성 스트리밍 응답을 그대로 반환한다`() {
        val api = FakeInterviewApi()
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.streamAudio(42L, 1L) }

        assertNotNull(actual)
        assertEquals(42L, api.requestedStreamSessionId)
        assertEquals(1L, api.requestedStreamQuestionId)
    }

    @Test
    fun `면접 재개 상태 응답을 그대로 반환한다`() {
        val expected =
            InterviewResumeStatusResponseDto(
                resumeState = "NONE",
                startedAt = null,
                elapsedSeconds = null,
                status = null,
            )
        val api =
            FakeInterviewApi(getResumeResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.getResume(42L) }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedResumeSessionId)
    }

    @Test
    fun `면접 재개 상태 data가 null이면 ServerException을 던진다`() {
        val api = FakeInterviewApi(getResumeResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getResume(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 재개 상태 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `면접 재개 확정 응답을 그대로 반환한다`() {
        val expected =
            InterviewResumeConfirmResponseDto(
                nextQuestion = null,
                sessionEnded = true,
                wrapUpMessage = null,
                endType = null,
                status = null,
                abandonCause = null,
                endedAt = null,
            )
        val api =
            FakeInterviewApi(
                confirmResumeResponse = ApiResponseDto(success = true, data = expected),
            )
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.confirmResume(42L) }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedConfirmResumeSessionId)
    }

    @Test
    fun `면접 재개 확정 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeInterviewApi(confirmResumeResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.confirmResume(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 재개 확정 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `면접 중단 응답을 그대로 반환한다`() {
        val expected =
            InterviewAbandonResponseDto(
                sessionId = 42L,
                status = "ABANDONED",
                abandonCause = "USER_REQUESTED",
                endedAt = "",
                ticketOutcome = "",
                reportGenerating = false,
            )
        val api =
            FakeInterviewApi(abandonResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            runBlocking {
                dataSource.abandon(42L, InterviewAbandonRequestDto(cause = "USER_REQUESTED"))
            }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedAbandonSessionId)
        assertEquals("USER_REQUESTED", api.requestedAbandonRequest?.cause)
    }

    @Test
    fun `면접 중단 data가 null이면 ServerException을 던진다`() {
        val api = FakeInterviewApi(abandonResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.abandon(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 중단 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `면접 레포트 상세 응답을 그대로 반환한다`() {
        val expected =
            InterviewReportResponseDto(
                status = "READY",
                headline = null,
                video = null,
                cards = null,
                script = null,
                guestFeedback = null,
            )
        val api =
            FakeInterviewApi(getReportResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.getReport(42L) }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedGetReportSessionId)
    }

    @Test
    fun `면접 레포트 상세 data가 null이면 ServerException을 던진다`() {
        val api = FakeInterviewApi(getReportResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getReport(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("면접 레포트 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `비디오 업로드 URL 발급 응답을 그대로 반환한다`() {
        val expected =
            InterviewVideoUploadUrlResponseDto(
                uploadUrl = "https://s3.example.com/upload",
                contentType = "video/mp4",
                expiresInSeconds = 3600,
            )
        val api =
            FakeInterviewApi(
                issueUploadUrlResponse = ApiResponseDto(success = true, data = expected),
            )
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.issueUploadUrl(42L) }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedIssueUploadUrlSessionId)
    }

    @Test
    fun `비디오 업로드 URL 발급 data가 null이면 ServerException을 던진다`() {
        val api =
            FakeInterviewApi(issueUploadUrlResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.issueUploadUrl(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("비디오 업로드 URL 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `비디오 업로드 완료 요청을 전달한다`() {
        val api = FakeInterviewApi()
        val dataSource = InterviewRemoteDataSourceImpl(api)
        val request = InterviewVideoCompleteRequestDto(wrapUpStartSec = 1.0f, wrapUpEndSec = 5.0f)

        runBlocking { dataSource.completeUpload(42L, request) }

        assertEquals(42L, api.requestedCompleteUploadSessionId)
        assertEquals(request, api.requestedCompleteUploadRequest)
    }

    @Test
    fun `비디오 만료 시간 응답을 그대로 반환한다`() {
        val expected =
            InterviewVideoExpiryResponseDto(
                expiresInSeconds = 1800,
                expired = false,
            )
        val api =
            FakeInterviewApi(getExpiryResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual = runBlocking { dataSource.getExpiry(42L) }

        assertSame(expected, actual)
        assertEquals(42L, api.requestedExpirySessionId)
    }

    @Test
    fun `비디오 만료 시간 data가 null이면 ServerException을 던진다`() {
        val api = FakeInterviewApi(getExpiryResponse = ApiResponseDto(success = true, data = null))
        val dataSource = InterviewRemoteDataSourceImpl(api)

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getExpiry(42L) }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("비디오 만료 시간 응답이 비어 있습니다.", actual.message)
    }

    @Suppress("LongParameterList")
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
        private val reportListResponse: ApiResponseDto<InterviewReportListResponseDto> =
            ApiResponseDto(
                success = true,
                data = InterviewReportListResponseDto(reports = emptyList()),
            ),
        private val submitAnswerResponse: ApiResponseDto<SubmitAnswerResponseDto> =
            ApiResponseDto(
                success = true,
                data = SubmitAnswerResponseDto(sessionEnded = true),
            ),
        private val getResumeResponse: ApiResponseDto<InterviewResumeStatusResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewResumeStatusResponseDto(
                        resumeState = "NONE",
                        startedAt = null,
                        elapsedSeconds = null,
                        status = null,
                    ),
            ),
        private val confirmResumeResponse: ApiResponseDto<InterviewResumeConfirmResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewResumeConfirmResponseDto(
                        nextQuestion = null,
                        sessionEnded = true,
                        wrapUpMessage = null,
                        endType = null,
                        status = null,
                        abandonCause = null,
                        endedAt = null,
                    ),
            ),
        private val abandonResponse: ApiResponseDto<InterviewAbandonResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewAbandonResponseDto(
                        sessionId = 1L,
                        status = "ABANDONED",
                        abandonCause = "",
                        endedAt = "",
                        ticketOutcome = "",
                        reportGenerating = false,
                    ),
            ),
        private val getReportResponse: ApiResponseDto<InterviewReportResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewReportResponseDto(
                        status = "READY",
                        headline = null,
                        video = null,
                        cards = null,
                        script = null,
                        guestFeedback = null,
                    ),
            ),
        private val issueUploadUrlResponse: ApiResponseDto<InterviewVideoUploadUrlResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    InterviewVideoUploadUrlResponseDto(
                        uploadUrl = "",
                        contentType = "",
                        expiresInSeconds = 0,
                    ),
            ),
        private val getExpiryResponse: ApiResponseDto<InterviewVideoExpiryResponseDto> =
            ApiResponseDto(
                success = true,
                data = InterviewVideoExpiryResponseDto(expiresInSeconds = 0, expired = false),
            ),
    ) : InterviewApi {
        var requestedJdUrl: String? = null
            private set
        var requestedCreateSession: CreateInterviewSessionRequestDto? = null
            private set
        var requestedSessionId: Long? = null
            private set
        var requestedSubmitSessionId: Long? = null
            private set
        var requestedSubmitQuestionId: Long? = null
            private set
        var requestedStreamSessionId: Long? = null
            private set
        var requestedStreamQuestionId: Long? = null
            private set
        var requestedResumeSessionId: Long? = null
            private set
        var requestedConfirmResumeSessionId: Long? = null
            private set
        var requestedAbandonSessionId: Long? = null
            private set
        var requestedAbandonRequest: InterviewAbandonRequestDto? = null
            private set
        var requestedGetReportSessionId: Long? = null
            private set
        var requestedIssueUploadUrlSessionId: Long? = null
            private set
        var requestedCompleteUploadSessionId: Long? = null
            private set
        var requestedCompleteUploadRequest: InterviewVideoCompleteRequestDto? = null
            private set
        var requestedExpirySessionId: Long? = null
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

        override suspend fun getReportList(): ApiResponseDto<InterviewReportListResponseDto> =
            reportListResponse

        override suspend fun submitAnswer(
            sessionId: Long,
            questionId: Long,
            isWrapUp: Boolean,
            questionAudioStartAt: Float?,
            questionAudioEndAt: Float?,
            answerStartAt: Float?,
            answerEndAt: Float?,
            answerDuration: Float?,
            endType: String?,
            audio: MultipartBody.Part?,
        ): ApiResponseDto<SubmitAnswerResponseDto> {
            requestedSubmitSessionId = sessionId
            requestedSubmitQuestionId = questionId
            return submitAnswerResponse
        }

        override suspend fun streamAudio(
            sessionId: Long,
            questionId: Long,
        ): ResponseBody {
            requestedStreamSessionId = sessionId
            requestedStreamQuestionId = questionId
            return ResponseBody.create(null, "")
        }

        override suspend fun getResume(
            sessionId: Long,
        ): ApiResponseDto<InterviewResumeStatusResponseDto> {
            requestedResumeSessionId = sessionId
            return getResumeResponse
        }

        override suspend fun confirmResume(
            sessionId: Long,
        ): ApiResponseDto<InterviewResumeConfirmResponseDto> {
            requestedConfirmResumeSessionId = sessionId
            return confirmResumeResponse
        }

        override suspend fun abandon(
            sessionId: Long,
            request: InterviewAbandonRequestDto?,
        ): ApiResponseDto<InterviewAbandonResponseDto> {
            requestedAbandonSessionId = sessionId
            requestedAbandonRequest = request
            return abandonResponse
        }

        override suspend fun getReport(
            sessionId: Long,
        ): ApiResponseDto<InterviewReportResponseDto> {
            requestedGetReportSessionId = sessionId
            return getReportResponse
        }

        override suspend fun issueUploadUrl(
            sessionId: Long,
        ): ApiResponseDto<InterviewVideoUploadUrlResponseDto> {
            requestedIssueUploadUrlSessionId = sessionId
            return issueUploadUrlResponse
        }

        override suspend fun completeUpload(
            sessionId: Long,
            request: InterviewVideoCompleteRequestDto?,
        ): ApiResponseDto<Unit> {
            requestedCompleteUploadSessionId = sessionId
            requestedCompleteUploadRequest = request
            return ApiResponseDto(success = true, data = Unit)
        }

        override suspend fun getExpiry(
            sessionId: Long,
        ): ApiResponseDto<InterviewVideoExpiryResponseDto> {
            requestedExpirySessionId = sessionId
            return getExpiryResponse
        }
    }

    private companion object {
        fun sampleRequest(): CreateInterviewSessionRequestDto =
            CreateInterviewSessionRequestDto(
                portfolioId = "portfolio-1",
                jdUrl = "https://example.com/jd",
            )
    }
}

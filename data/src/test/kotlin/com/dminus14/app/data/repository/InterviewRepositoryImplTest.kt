package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.config.NetworkConfig
import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.dto.feedback.FeedbackShareCreateResponseDto
import com.dminus14.app.data.remote.dto.interview.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewReportListResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewReportResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewResumeConfirmResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewResumeStatusResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoCompleteRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoExpiryResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoUploadUrlResponseDto
import com.dminus14.app.data.remote.dto.interview.SubmitAnswerResponseDto
import com.dminus14.app.data.remote.dto.jd.JdValidateResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.AccountSuspendedException
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.FreeTextNotRelevantException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidFreeTextLengthException
import com.dminus14.app.domain.exception.InvalidJdLengthException
import com.dminus14.app.domain.exception.InvalidJdUrlException
import com.dminus14.app.domain.exception.JdContentNotFoundException
import com.dminus14.app.domain.exception.JdNotValidatedException
import com.dminus14.app.domain.exception.JdUrlAndTextBothProvidedException
import com.dminus14.app.domain.exception.JdValidationLimitExceededException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.NoRemainingTicketException
import com.dminus14.app.domain.exception.PortfolioNotFoundException
import com.dminus14.app.domain.exception.PortfolioProcessingException
import com.dminus14.app.domain.exception.PortfolioUploadFailedException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.UserProfileNotRegisteredException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.InterviewAbandonRequestCause
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.model.InterviewResumeState
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.dminus14.app.domain.model.SubmitInterviewAnswerCommand
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun `면접 리포트 목록 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getReportList() }

        assertNotNull(actual)
        assertTrue(dataSource.getReportListCalled)
    }

    @Test
    fun `답변 제출을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual =
            runBlocking {
                repository.submitAnswer(
                    SubmitInterviewAnswerCommand(
                        sessionId = 42L,
                        questionId = 1L,
                        isWrapUp = false,
                    ),
                )
            }

        assertTrue(actual.sessionEnded)
        assertEquals(42L, dataSource.requestedSubmitSessionId)
        assertEquals(1L, dataSource.requestedSubmitQuestionId)
    }

    @Test
    fun `음성 스트리밍 URL을 올바른 포맷으로 생성한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)
        val baseUrl = NetworkConfig.BASE_URL.removeSuffix("/")

        val actual = repository.getAudioStreamUrl(sessionId = 42L, questionId = 1L)

        assertEquals("$baseUrl/api/v1/interview/sessions/42/questions/1/audio/stream", actual)
    }

    @Test
    fun `면접 재개 상태 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getResume(42L) }

        assertEquals(InterviewResumeState.Unknown("NONE"), actual.resumeState)
        assertEquals(42L, dataSource.requestedGetResumeSessionId)
    }

    @Test
    fun `면접 재개 확정을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.confirmResume(42L) }

        assertTrue(actual.sessionEnded)
        assertEquals(42L, dataSource.requestedConfirmResumeSessionId)
    }

    @Test
    fun `면접 중단을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual =
            runBlocking {
                repository.abandon(42L, InterviewAbandonRequestCause.UserExit)
            }

        assertEquals(42L, actual.sessionId)
        assertEquals(com.dminus14.app.domain.model.InterviewTerminalStatus.Abandoned, actual.status)
        assertEquals(42L, dataSource.requestedAbandonSessionId)
        assertEquals("USER_EXIT", dataSource.requestedAbandonRequest?.cause)
    }

    @Test
    fun `면접 리포트 상세 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getReport(42L) }

        assertEquals(InterviewReportStatus.READY, actual.status)
        assertEquals(42L, dataSource.requestedGetReportSessionId)
    }

    @Test
    fun `비디오 업로드 URL 발급을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.issueUploadUrl(42L) }

        assertEquals("https://s3.example.com/upload", actual.uploadUrl)
        assertEquals(42L, dataSource.requestedIssueUploadUrlSessionId)
    }

    @Test
    fun `비디오 업로드 완료 보고를 위임한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        runBlocking { repository.completeUpload(42L, wrapUpStartSec = 1.0f, wrapUpEndSec = 5.0f) }

        assertEquals(42L, dataSource.requestedCompleteUploadSessionId)
        assertEquals(1.0f, dataSource.requestedCompleteUploadRequest?.wrapUpStartSec)
        assertEquals(5.0f, dataSource.requestedCompleteUploadRequest?.wrapUpEndSec)
    }

    @Test
    fun `비디오 만료 시간 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeInterviewRemoteDataSource()
        val repository = InterviewRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getExpiry(42L) }

        assertEquals(1800, actual.expiresInSeconds)
        assertEquals(false, actual.expired)
        assertEquals(42L, dataSource.requestedGetExpirySessionId)
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
                ApiErrorCode.USER_PROFILE_NOT_REGISTERED to
                    UserProfileNotRegisteredException::class.java,
                ApiErrorCode.JD_URL_AND_TEXT_BOTH_PROVIDED to
                    JdUrlAndTextBothProvidedException::class.java,
                ApiErrorCode.JD_NOT_VALIDATED to JdNotValidatedException::class.java,
                ApiErrorCode.JD_CONTENT_NOT_FOUND to JdContentNotFoundException::class.java,
                ApiErrorCode.INVALID_JD_LENGTH to InvalidJdLengthException::class.java,
                ApiErrorCode.INVALID_FREETEXT_LENGTH to InvalidFreeTextLengthException::class.java,
                ApiErrorCode.FREETEXT_NOT_RELEVANT to FreeTextNotRelevantException::class.java,
                ApiErrorCode.PORTFOLIO_NOT_FOUND to PortfolioNotFoundException::class.java,
                ApiErrorCode.PORTFOLIO_PROCESSING to PortfolioProcessingException::class.java,
                ApiErrorCode.PORTFOLIO_UPLOAD_FAILED to PortfolioUploadFailedException::class.java,
                ApiErrorCode.NO_REMAINING_TICKET to NoRemainingTicketException::class.java,
                ApiErrorCode.ACCOUNT_SUSPENDED to AccountSuspendedException::class.java,
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
        var getReportListCalled: Boolean = false
            private set
        var requestedSubmitSessionId: Long? = null
            private set
        var requestedSubmitQuestionId: Long? = null
            private set
        var requestedGetResumeSessionId: Long? = null
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
        var requestedGetExpirySessionId: Long? = null
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

        override suspend fun getReportList(): InterviewReportListResponseDto {
            failure?.let { throw it }
            getReportListCalled = true
            return InterviewReportListResponseDto(reports = emptyList())
        }

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
            audio: okhttp3.MultipartBody.Part?,
        ): SubmitAnswerResponseDto {
            failure?.let { throw it }
            requestedSubmitSessionId = sessionId
            requestedSubmitQuestionId = questionId
            return SubmitAnswerResponseDto(sessionEnded = true)
        }

        override suspend fun streamAudio(
            sessionId: Long,
            questionId: Long,
        ): okhttp3.ResponseBody {
            failure?.let { throw it }
            return "".toResponseBody(null)
        }

        override suspend fun getResume(sessionId: Long): InterviewResumeStatusResponseDto {
            failure?.let { throw it }
            requestedGetResumeSessionId = sessionId
            return InterviewResumeStatusResponseDto(
                resumeState = "NONE",
                startedAt = null,
                elapsedSeconds = null,
                status = null,
            )
        }

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirmResponseDto {
            failure?.let { throw it }
            requestedConfirmResumeSessionId = sessionId
            return InterviewResumeConfirmResponseDto(
                nextQuestion = null,
                sessionEnded = true,
                wrapUpMessage = null,
                endType = null,
                status = null,
                abandonCause = null,
                endedAt = null,
            )
        }

        override suspend fun abandon(
            sessionId: Long,
            request: InterviewAbandonRequestDto?,
        ): InterviewAbandonResponseDto {
            failure?.let { throw it }
            requestedAbandonSessionId = sessionId
            requestedAbandonRequest = request
            return InterviewAbandonResponseDto(
                sessionId = sessionId,
                status = "ABANDONED",
                abandonCause = request?.cause.orEmpty(),
                endedAt = "",
                ticketOutcome = "",
                reportGenerating = false,
            )
        }

        override suspend fun getReport(sessionId: Long): InterviewReportResponseDto {
            failure?.let { throw it }
            requestedGetReportSessionId = sessionId
            return InterviewReportResponseDto(
                status = "READY",
                headline = null,
                video = null,
                cards = null,
                script = null,
                guestFeedback = null,
            )
        }

        override suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrlResponseDto {
            failure?.let { throw it }
            requestedIssueUploadUrlSessionId = sessionId
            return InterviewVideoUploadUrlResponseDto(
                uploadUrl = "https://s3.example.com/upload",
                contentType = "video/mp4",
                expiresInSeconds = 3600,
            )
        }

        override suspend fun completeUpload(
            sessionId: Long,
            request: InterviewVideoCompleteRequestDto?,
        ) {
            failure?.let { throw it }
            requestedCompleteUploadSessionId = sessionId
            requestedCompleteUploadRequest = request
        }

        override suspend fun getExpiry(sessionId: Long): InterviewVideoExpiryResponseDto {
            failure?.let { throw it }
            requestedGetExpirySessionId = sessionId
            return InterviewVideoExpiryResponseDto(
                expiresInSeconds = 1800,
                expired = false,
            )
        }

        override suspend fun createFeedbackShare(
            sessionId: Long,
            axes: List<String>,
        ): FeedbackShareCreateResponseDto {
            failure?.let { throw it }
            return FeedbackShareCreateResponseDto(token = "fake-token")
        }

        override suspend fun closeFeedbackShare(sessionId: Long) {
            failure?.let { throw it }
        }
    }

    private companion object {
        fun sampleRequest(): InterviewSessionRequest =
            InterviewSessionRequest(
                portfolioId = "portfolio-1",
                jdUrl = "https://example.com/jd",
            )
    }
}

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportList
import com.dminus14.app.domain.model.InterviewResumeConfirm
import com.dminus14.app.domain.model.InterviewResumeStatus
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewVideoExpiry
import com.dminus14.app.domain.model.InterviewVideoUploadUrl
import com.dminus14.app.domain.model.JdValidationResult
import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.repository.InterviewRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class GetInterviewResumeUseCaseTest {
    @Test
    fun `면접 재개 상태 조회를 성공하면 결과를 반환한다`() =
        runTest {
            val expected =
                InterviewResumeStatus(
                    resumeState = "PAUSED",
                    startedAt = "2026-08-04T10:00:00Z",
                    elapsedSeconds = 120,
                    status = "IN_PROGRESS",
                )
            val repository = FakeInterviewRepository(resumeStatusResult = expected)
            val useCase = GetInterviewResumeUseCase(repository)

            val result = useCase(42L)

            assertTrue(result.isSuccess)
            assertSame(expected, result.getOrThrow())
            assertEquals(1, repository.getResumeCallCount)
            assertEquals(42L, repository.requestedSessionId)
        }

    @Test
    fun `면접 재개 상태 조회 중 예외가 발생하면 실패 결과를 반환한다`() =
        runTest {
            val exception = RuntimeException("재개 상태 조회 실패")
            val repository = FakeInterviewRepository(failure = exception)
            val useCase = GetInterviewResumeUseCase(repository)

            val result = useCase(42L)

            assertTrue(result.isFailure)
            assertSame(exception, result.exceptionOrNull())
        }

    private open class FakeInterviewRepository(
        private val resumeStatusResult: InterviewResumeStatus =
            InterviewResumeStatus(
                resumeState = "NONE",
                startedAt = null,
                elapsedSeconds = null,
                status = null,
            ),
        private val failure: Throwable? = null,
    ) : InterviewRepository {
        var getResumeCallCount = 0
            private set
        var requestedSessionId: Long? = null
            private set

        override suspend fun validateJdUrl(jdUrl: String): JdValidationResult = TODO()

        override suspend fun createInterviewSession(
            request: InterviewSessionRequest,
        ): InterviewSessionResult = TODO()

        override suspend fun getInterviewSession(sessionId: Long): InterviewSessionStatus = TODO()

        override suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatus =
            TODO()

        override suspend fun getReportList(): InterviewReportList = TODO()

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
            audioFile: File?,
        ): SubmitAnswerResult = TODO()

        override fun getAudioStreamUrl(
            sessionId: Long,
            questionId: Long,
        ): String = TODO()

        override suspend fun getResume(sessionId: Long): InterviewResumeStatus {
            getResumeCallCount++
            requestedSessionId = sessionId
            failure?.let { throw it }
            return resumeStatusResult
        }

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirm = TODO()

        override suspend fun abandon(
            sessionId: Long,
            cause: String,
        ): InterviewAbandon = TODO()

        override suspend fun getReport(sessionId: Long): InterviewReport = TODO()

        override suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrl = TODO()

        override suspend fun completeUpload(
            sessionId: Long,
            wrapUpStartSec: Float?,
            wrapUpEndSec: Float?,
        ) = TODO()

        override suspend fun getExpiry(sessionId: Long): InterviewVideoExpiry = TODO()
    }
}

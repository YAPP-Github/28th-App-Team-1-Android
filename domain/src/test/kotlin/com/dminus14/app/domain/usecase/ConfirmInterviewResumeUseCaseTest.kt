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

class ConfirmInterviewResumeUseCaseTest {
    @Test
    fun `면접 재개 확정을 성공하면 결과를 반환한다`() =
        runTest {
            val expected =
                InterviewResumeConfirm(
                    nextQuestion = null,
                    sessionEnded = true,
                    wrapUpMessage = null,
                    endType = null,
                    status = null,
                    abandonCause = null,
                    endedAt = null,
                )
            val repository = FakeInterviewRepository(confirmResult = expected)
            val useCase = ConfirmInterviewResumeUseCase(repository)

            val result = useCase(42L)

            assertTrue(result.isSuccess)
            assertSame(expected, result.getOrThrow())
            assertEquals(1, repository.confirmResumeCallCount)
            assertEquals(42L, repository.requestedSessionId)
        }

    @Test
    fun `면접 재개 확정 중 예외가 발생하면 실패 결과를 반환한다`() =
        runTest {
            val exception = RuntimeException("재개 확정 실패")
            val repository = FakeInterviewRepository(failure = exception)
            val useCase = ConfirmInterviewResumeUseCase(repository)

            val result = useCase(42L)

            assertTrue(result.isFailure)
            assertSame(exception, result.exceptionOrNull())
        }

    private open class FakeInterviewRepository(
        private val confirmResult: InterviewResumeConfirm =
            InterviewResumeConfirm(
                nextQuestion = null,
                sessionEnded = true,
                wrapUpMessage = null,
                endType = null,
                status = null,
                abandonCause = null,
                endedAt = null,
            ),
        private val failure: Throwable? = null,
    ) : InterviewRepository {
        var confirmResumeCallCount = 0
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

        override suspend fun getResume(sessionId: Long): InterviewResumeStatus = TODO()

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirm {
            confirmResumeCallCount++
            requestedSessionId = sessionId
            failure?.let { throw it }
            return confirmResult
        }

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

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.model.InterviewAbandonRequestCause
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportList
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.model.InterviewResumeConfirm
import com.dminus14.app.domain.model.InterviewResumeStatus
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewVideoExpiry
import com.dminus14.app.domain.model.InterviewVideoUploadUrl
import com.dminus14.app.domain.model.JdValidationResult
import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.model.SubmitInterviewAnswerCommand
import com.dminus14.app.domain.model.UploadInterviewVideoCommand
import com.dminus14.app.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveInterviewReportUseCaseTest {
    @Test
    fun `첫 응답이 READY 이면 1회만 방출하고 종료한다`() =
        runTest {
            val repository =
                FakeReportRepository(
                    responses = listOf(readyReport()),
                )
            val useCase = ObserveInterviewReportUseCase(repository)

            val emissions = useCase(sessionId = 1L).toList()

            assertEquals(1, emissions.size)
            assertEquals(InterviewReportStatus.READY, emissions.single().status)
            assertEquals(1, repository.getReportCallCount)
        }

    @Test
    fun `GENERATING 이 여러 번 이어져도 READY 가 오면 그 시점에 종료한다`() =
        runTest {
            val repository =
                FakeReportRepository(
                    responses = listOf(generatingReport(), generatingReport(), readyReport()),
                )
            val useCase = ObserveInterviewReportUseCase(repository)

            val emissions = useCase(sessionId = 1L).toList()

            assertEquals(3, emissions.size)
            assertEquals(
                listOf(
                    InterviewReportStatus.GENERATING,
                    InterviewReportStatus.GENERATING,
                    InterviewReportStatus.READY,
                ),
                emissions.map { it.status },
            )
        }

    @Test
    fun `최대 시도 횟수를 넘도록 GENERATING 이 이어지면 FAILED 로 강제 방출한다`() =
        runTest {
            val responses =
                List(ObserveInterviewReportUseCase.MAX_ATTEMPTS + 1) { generatingReport() }
            val repository = FakeReportRepository(responses = responses)
            val useCase = ObserveInterviewReportUseCase(repository)

            val emissions = useCase(sessionId = 1L).toList()

            val lastEmission = emissions.last()
            assertEquals(InterviewReportStatus.FAILED, lastEmission.status)
            assertEquals(ObserveInterviewReportUseCase.MAX_ATTEMPTS, repository.getReportCallCount)
        }

    private fun readyReport(): InterviewReport =
        InterviewReport(
            status = InterviewReportStatus.READY,
            headline = null,
            video = null,
            cards = null,
            script = null,
            guestFeedback = null,
        )

    private fun generatingReport(): InterviewReport =
        InterviewReport(
            status = InterviewReportStatus.GENERATING,
            headline = null,
            video = null,
            cards = null,
            script = null,
            guestFeedback = null,
        )

    private class FakeReportRepository(
        private val responses: List<InterviewReport>,
    ) : InterviewRepository {
        var getReportCallCount: Int = 0
            private set

        override suspend fun getReport(sessionId: Long): InterviewReport {
            val index = getReportCallCount.coerceAtMost(responses.lastIndex)
            getReportCallCount += 1
            return responses[index]
        }

        override suspend fun validateJdUrl(jdUrl: String): JdValidationResult = error("사용하지 않음")

        override suspend fun createInterviewSession(
            request: InterviewSessionRequest,
        ): InterviewSessionResult = error("사용하지 않음")

        override suspend fun getInterviewSession(sessionId: Long): InterviewSessionStatus =
            error("사용하지 않음")

        override suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatus =
            error("사용하지 않음")

        override suspend fun getReportList(): InterviewReportList = error("사용하지 않음")

        override suspend fun submitAnswer(
            command: SubmitInterviewAnswerCommand,
        ): SubmitAnswerResult = error("사용하지 않음")

        override fun getAudioStreamUrl(
            sessionId: Long,
            questionId: Long,
        ): String = error("사용하지 않음")

        override suspend fun getResume(sessionId: Long): InterviewResumeStatus = error("사용하지 않음")

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirm =
            error("사용하지 않음")

        override suspend fun abandon(
            sessionId: Long,
            cause: InterviewAbandonRequestCause,
        ): InterviewAbandon = error("사용하지 않음")

        override suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrl =
            error("사용하지 않음")

        override suspend fun uploadVideo(command: UploadInterviewVideoCommand) = error("사용하지 않음")

        override suspend fun completeUpload(
            sessionId: Long,
            wrapUpStartSec: Float?,
            wrapUpEndSec: Float?,
        ) = error("사용하지 않음")

        override suspend fun getExpiry(sessionId: Long): InterviewVideoExpiry = error("사용하지 않음")
    }
}

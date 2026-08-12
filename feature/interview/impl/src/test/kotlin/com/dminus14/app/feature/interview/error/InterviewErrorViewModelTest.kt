package com.dminus14.app.feature.interview.error

import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.model.InterviewAbandonCause
import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.model.InterviewTerminalStatus
import com.dminus14.app.domain.model.InterviewTicketOutcome
import com.dminus14.app.domain.model.InterviewUploadTaskStatus
import com.dminus14.app.domain.usecase.AbandonInterviewUseCase
import com.dminus14.app.domain.usecase.ConfirmInterviewResumeUseCase
import com.dminus14.app.domain.usecase.GetInterviewMediaManifestUseCase
import com.dminus14.app.domain.usecase.GetInterviewProgressUseCase
import com.dminus14.app.domain.usecase.GetInterviewResumeUseCase
import com.dminus14.app.domain.usecase.RetainInterviewSessionForCleanupUseCase
import com.dminus14.app.domain.usecase.SavePendingInterviewAnswerUseCase
import com.dminus14.app.domain.usecase.SubmitAnswerUseCase
import com.dminus14.app.feature.interview.api.InterviewErrorType
import com.dminus14.app.feature.interview.interview.InterviewRecoveryStore
import com.dminus14.app.feature.interview.interview.InterviewViewModelTestFixture
import com.dminus14.app.feature.interview.interview.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InterviewErrorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `중단 응답이 리포트를 만들지 않아도 미디어를 보존 기한 정리 대상으로 남긴다`() =
        runTest(mainDispatcherRule.dispatcher) {
            val fixture = InterviewViewModelTestFixture()
            val sessionId = InterviewViewModelTestFixture.SESSION_ID
            fixture.manifests[sessionId] = InterviewMediaManifest(sessionId = sessionId)
            fixture.abandonResult =
                InterviewAbandon(
                    sessionId = sessionId,
                    status = InterviewTerminalStatus.Abandoned,
                    abandonCause = InterviewAbandonCause.UserExit,
                    endedAt = "",
                    ticketOutcome = InterviewTicketOutcome.Committed,
                    reportGenerating = false,
                )
            val viewModel = createViewModel(fixture)

            viewModel.onIntent(InterviewErrorIntent.Load(InterviewErrorType.SERVER_TEMPORARY))
            runCurrent()
            viewModel.onIntent(InterviewErrorIntent.ClickAbort)
            runCurrent()

            assertNull(fixture.progress)
            assertNotNull(fixture.manifests[sessionId])
            assertEquals(
                InterviewUploadTaskStatus.RETAINED,
                fixture.uploadTasks.values
                    .single()
                    .status,
            )
        }

    private fun createViewModel(fixture: InterviewViewModelTestFixture) =
        InterviewErrorViewModel(
            getProgress = GetInterviewProgressUseCase(fixture.localRepository),
            getManifest = GetInterviewMediaManifestUseCase(fixture.localRepository),
            getResume = GetInterviewResumeUseCase(fixture.interviewRepository),
            confirmResume = ConfirmInterviewResumeUseCase(fixture.interviewRepository),
            abandonInterview = AbandonInterviewUseCase(fixture.interviewRepository),
            submitAnswer = SubmitAnswerUseCase(fixture.interviewRepository),
            savePendingAnswer = SavePendingInterviewAnswerUseCase(fixture.localRepository),
            retainSessionForCleanup =
                RetainInterviewSessionForCleanupUseCase(fixture.localRepository),
            recoveryStore = InterviewRecoveryStore(),
        )
}

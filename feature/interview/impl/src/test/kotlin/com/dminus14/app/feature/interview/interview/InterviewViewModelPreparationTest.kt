package com.dminus14.app.feature.interview.interview

import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.dminus14.app.domain.model.QuestionTurn
import com.dminus14.app.domain.model.SummaryQuestion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InterviewViewModelPreparationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `장치와 저장 공간 및 서버 준비가 완료되면 시작 안내 상태가 된다`() =
        runTest(mainDispatcherRule.dispatcher) {
            val fixture = InterviewViewModelTestFixture()
            fixture.sessionStatus =
                InterviewSessionStatus(
                    status = InterviewSessionStatusType.READY,
                    startedAt = null,
                    summaryQuestion =
                        SummaryQuestion(
                            questionId = 1L,
                            ttsAudio = null,
                            turn = QuestionTurn(turnLevel = 1, depthLevel = 0),
                        ),
                )
            val viewModel = fixture.createViewModel()

            viewModel.onIntent(InterviewIntent.LoadInterview)
            runCurrent()
            viewModel.onIntent(InterviewIntent.ReportCameraPermissionGranted)
            viewModel.onIntent(InterviewIntent.ReportCameraReady)
            viewModel.onIntent(InterviewIntent.ReportMicrophoneReady)
            viewModel.onIntent(InterviewIntent.ReportStorageAvailability(Long.MAX_VALUE))

            assertEquals(InterviewScreenState.START_GUIDE, viewModel.state.value.screenState)
            assertEquals(1L, viewModel.state.value.questionId)
            assertTrue(viewModel.state.value.isReadyToStart)
        }
}

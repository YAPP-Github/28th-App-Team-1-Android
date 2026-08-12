package com.dminus14.app.feature.interviewreport.guestfeedback

import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.usecase.CreateFeedbackShareUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GuestFeedbackRequestViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `기본값은 5개 항목이 모두 선택된 상태다`() {
        val viewModel = viewModel(FakeFeedbackShareRepository())

        assertEquals(GuestFeedbackAxisCode.entries.toSet(), viewModel.state.value.selectedAxes)
    }

    @Test
    fun `마지막 한 개 항목은 해제되지 않는다`() {
        val viewModel = viewModel(FakeFeedbackShareRepository())
        // GAZE 하나만 남기고 나머지를 모두 해제한다.
        (GuestFeedbackAxisCode.entries - GuestFeedbackAxisCode.GAZE).forEach { axis ->
            viewModel.onIntent(GuestFeedbackRequestIntent.ToggleAxis(axis))
        }
        assertEquals(setOf(GuestFeedbackAxisCode.GAZE), viewModel.state.value.selectedAxes)

        viewModel.onIntent(GuestFeedbackRequestIntent.ToggleAxis(GuestFeedbackAxisCode.GAZE))

        assertEquals(setOf(GuestFeedbackAxisCode.GAZE), viewModel.state.value.selectedAxes)
    }

    @Test
    fun `종료하기 성공 시 공유 링크가 채워진다`() =
        runTest {
            val viewModel = viewModel(FakeFeedbackShareRepository(token = "abc123"))
            viewModel.bindSessionId(7L)

            viewModel.onIntent(GuestFeedbackRequestIntent.ClickSubmit)
            advanceUntilIdle()

            val link = viewModel.state.value.shareLink
            assertTrue("링크가 토큰을 포함해야 한다: $link", link?.endsWith("abc123") == true)
        }

    private fun viewModel(repository: FeedbackShareRepository): GuestFeedbackRequestViewModel =
        GuestFeedbackRequestViewModel(CreateFeedbackShareUseCase(repository))
}

private class FakeFeedbackShareRepository(
    private val token: String = "token",
) : FeedbackShareRepository {
    override suspend fun createShare(
        sessionId: Long,
        axes: List<GuestFeedbackAxisCode>,
    ): String = token
}

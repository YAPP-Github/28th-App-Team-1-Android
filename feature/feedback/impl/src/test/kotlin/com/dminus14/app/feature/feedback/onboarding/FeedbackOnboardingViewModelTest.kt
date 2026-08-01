package com.dminus14.app.feature.feedback.onboarding

import com.dminus14.app.domain.usecase.EnterGuestFeedbackUseCase
import com.dminus14.app.feature.feedback.FakeGuestFeedbackRepository
import com.dminus14.app.feature.feedback.MainDispatcherRule
import com.dminus14.app.feature.feedback.api.FeedbackOnboarding
import com.dminus14.app.feature.feedback.openEntry
import com.dminus14.app.feature.feedback.session.GuestFeedbackFlowSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackOnboardingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `열린 공유를 한 번 조회하고 민감한 진입 데이터를 메모리 세션에 보관한다`() =
        runTest {
            val repository = FakeGuestFeedbackRepository()
            val session = GuestFeedbackFlowSession()
            val viewModel =
                FeedbackOnboardingViewModel(EnterGuestFeedbackUseCase(repository), session)

            viewModel.onIntent(FeedbackOnboardingIntent.Load("synthetic-token"))
            viewModel.onIntent(FeedbackOnboardingIntent.Load("synthetic-token"))
            runCurrent()

            assertEquals(1, repository.enterCount)
            assertEquals("합성 요청자", viewModel.state.value.requesterName)
            assertTrue(viewModel.state.value.hasLoaded)
            assertEquals(openEntry().videoUrl, session.snapshot()?.videoUrl)
        }

    @Test
    fun `유효한 별칭만 확정해 세션에 저장하고 평가 준비 효과를 발행한다`() =
        runTest {
            val session = GuestFeedbackFlowSession().apply { start("synthetic-token", openEntry()) }
            val viewModel =
                FeedbackOnboardingViewModel(
                    EnterGuestFeedbackUseCase(FakeGuestFeedbackRepository()),
                    session,
                )
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(FeedbackOnboardingIntent.NicknameChanged("  합성 지인  "))
            assertTrue(viewModel.state.value.canContinue)
            viewModel.onIntent(FeedbackOnboardingIntent.NicknameConfirmed)
            runCurrent()

            assertEquals("합성 지인", session.snapshot()?.nickname)
            assertEquals(FeedbackOnboardingEffect.FeedbackReady, effect.await())
        }

    @Test
    fun `빈 값과 줄바꿈과 열두 자 초과 별칭은 다음 진행을 차단한다`() {
        listOf("", "합성\n지인", "가".repeat(13)).forEach { nickname ->
            val state = FeedbackOnboardingState(nickname = nickname)
            assertFalse(state.canContinue)
        }
        assertTrue(FeedbackOnboardingState(nickname = "가".repeat(12)).canContinue)
    }

    @Test
    fun `시작 경로 문자열은 공유 토큰 원문을 노출하지 않는다`() {
        val route = FeedbackOnboarding("sensitive-synthetic-token")

        assertFalse(route.toString().contains("sensitive-synthetic-token"))
        assertTrue(route.toString().contains("redacted"))
    }
}

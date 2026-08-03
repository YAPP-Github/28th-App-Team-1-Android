package com.dminus14.app.feature.feedback.feedback

import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.globalModalEvents
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.feature.feedback.MainDispatcherRule
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
class FeedbackViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `지정된 축만 세션 순서로 불러오고 모든 축 평가 뒤 검토를 허용한다`() {
        val session = session()
        val viewModel = FeedbackViewModel(session)

        viewModel.onIntent(FeedbackIntent.LoadSession)
        assertEquals(
            listOf("시선", "목소리"),
            viewModel.state.value.axes
                .map { it.title },
        )
        assertFalse(viewModel.state.value.canReview)

        viewModel.onIntent(FeedbackIntent.RatingSelected(GuestFeedbackAxisCode.GAZE, 4))
        viewModel.onIntent(FeedbackIntent.RatingSelected(GuestFeedbackAxisCode.VOICE, 1))

        assertTrue(viewModel.state.value.canReview)
        assertEquals(
            4,
            session
                .snapshot()
                ?.ratings
                ?.get(GuestFeedbackAxisCode.GAZE)
                ?.level,
        )
    }

    @Test
    fun `코멘트는 다음에서만 저장하고 닫으면 편집 전 값을 유지한다`() {
        val session = session()
        val viewModel = FeedbackViewModel(session)
        viewModel.onIntent(FeedbackIntent.LoadSession)

        viewModel.onIntent(FeedbackIntent.CommentEditorClicked(GuestFeedbackAxisCode.GAZE))
        viewModel.onIntent(FeedbackIntent.CommentChanged("버릴 합성 코멘트"))
        viewModel.onIntent(FeedbackIntent.CommentDismissed)
        assertEquals(
            "",
            viewModel.state.value.axes
                .first()
                .comment,
        )

        viewModel.onIntent(FeedbackIntent.CommentEditorClicked(GuestFeedbackAxisCode.GAZE))
        viewModel.onIntent(FeedbackIntent.CommentChanged("저장할 합성 코멘트"))
        viewModel.onIntent(FeedbackIntent.CommentConfirmed)
        assertEquals(
            "저장할 합성 코멘트",
            viewModel.state.value.axes
                .first()
                .comment,
        )
    }

    @Test
    fun `최초 영상 안내 완료 여부만 상태에 남기고 재생 실행 상태는 두지 않는다`() {
        val viewModel = FeedbackViewModel(session())
        viewModel.onIntent(FeedbackIntent.LoadSession)

        assertTrue(viewModel.state.value.isVideoIntroVisible)
        viewModel.onIntent(FeedbackIntent.VideoIntroCompleted)
        assertFalse(viewModel.state.value.isVideoIntroVisible)
    }

    @Test
    fun `축을 선택하면 영상을 분할하고 확대하면 선택값을 유지한다`() {
        val viewModel = FeedbackViewModel(session())
        viewModel.onIntent(FeedbackIntent.LoadSession)

        viewModel.onIntent(FeedbackIntent.AxisSelected(GuestFeedbackAxisCode.GAZE))
        assertFalse(viewModel.state.value.isVideoExpanded)

        viewModel.onIntent(FeedbackIntent.VideoExpanded)
        assertTrue(viewModel.state.value.isVideoExpanded)
        assertEquals(GuestFeedbackAxisCode.GAZE, viewModel.state.value.selectedAxis)
    }

    @Test
    fun `축별 질문과 평가 선택지는 갱신된 피알디 순서를 따른다`() {
        val requesterName = "합성 요청자"

        assertEquals("합성 요청자님은 눈을 잘 마주치나요?", GuestFeedbackAxisCode.GAZE.question(requesterName))
        assertEquals(
            "표정이 안정되어 보이나요?",
            GuestFeedbackAxisCode.EXPRESSION.question(requesterName),
        )
        assertEquals(
            "합성 요청자님이 자세를 잘 유지하나요?",
            GuestFeedbackAxisCode.POSTURE.question(requesterName),
        )
        assertEquals(
            "손동작이 말과 잘 어울리나요?",
            GuestFeedbackAxisCode.GESTURE.question(requesterName),
        )
        assertEquals(
            "목소리가 선명하게 들리나요?",
            GuestFeedbackAxisCode.VOICE.question(requesterName),
        )
        assertEquals(
            listOf("안정됨", "꽤 안정됨", "가끔 굳음", "자주 굳음"),
            GuestFeedbackAxisCode.EXPRESSION.ratingOptions().map { it.label },
        )
        assertEquals(
            listOf("반듯함", "꽤 반듯함", "가끔 산만", "매우 산만"),
            GuestFeedbackAxisCode.POSTURE.ratingOptions().map { it.label },
        )
        assertEquals(
            listOf("잘 어울림", "꽤 어울림", "가끔 산만", "매우 산만"),
            GuestFeedbackAxisCode.GESTURE.ratingOptions().map { it.label },
        )
        assertEquals(
            listOf("잘 들림", "꽤 들림", "꽤 안 들림", "안 들림"),
            GuestFeedbackAxisCode.VOICE.ratingOptions().map { it.label },
        )
    }

    @Test
    fun `치명적 영상 오류는 평가를 차단하고 종료 확인 뒤 세션을 지운다`() =
        runTest {
            val session = session()
            val viewModel = FeedbackViewModel(session)
            val modal = async { globalModalEvents.first() }
            val effect = async { viewModel.effect.first() }
            runCurrent()

            viewModel.onIntent(FeedbackIntent.VideoPlaybackFailed)
            runCurrent()
            val event = modal.await()

            assertTrue(viewModel.state.value.isPlaybackBlocked)
            viewModel.onIntent(FeedbackIntent.RatingSelected(GuestFeedbackAxisCode.GAZE, 4))
            assertEquals(
                null,
                viewModel.state.value.axes
                    .first()
                    .level,
            )
            assertFalse(event.request.dismissible)
            event.complete(GlobalModalResult.Confirm)
            runCurrent()
            assertEquals(FeedbackEffect.ExitRequested, effect.await())
            assertEquals(null, session.snapshot())
        }

    private fun session() =
        GuestFeedbackFlowSession().apply {
            start("synthetic-token", openEntry())
            setNickname("합성 지인")
        }
}

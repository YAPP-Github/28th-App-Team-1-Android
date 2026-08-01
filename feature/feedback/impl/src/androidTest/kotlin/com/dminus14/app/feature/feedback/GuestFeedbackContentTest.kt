package com.dminus14.app.feature.feedback

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.feature.feedback.component.GuestFeedbackCommentModal
import com.dminus14.app.feature.feedback.feedback.FeedbackAxisUiModel
import com.dminus14.app.feature.feedback.feedback.FeedbackContent
import com.dminus14.app.feature.feedback.feedback.FeedbackIntent
import com.dminus14.app.feature.feedback.feedback.FeedbackState
import com.dminus14.app.feature.feedback.onboarding.FeedbackOnboardingContent
import com.dminus14.app.feature.feedback.onboarding.FeedbackOnboardingIntent
import com.dminus14.app.feature.feedback.onboarding.FeedbackOnboardingLoadState
import com.dminus14.app.feature.feedback.onboarding.FeedbackOnboardingState
import com.dminus14.app.feature.feedback.review.FeedbackReviewAxisUiModel
import com.dminus14.app.feature.feedback.review.FeedbackReviewBackHandler
import com.dminus14.app.feature.feedback.review.FeedbackReviewContent
import com.dminus14.app.feature.feedback.review.FeedbackReviewIntent
import com.dminus14.app.feature.feedback.review.FeedbackReviewState
import com.dminus14.designsystem.theme.HilitTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GuestFeedbackContentTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `온보딩 시작 버튼은 이름 입력을 요청한다`() {
        var receivedIntent: FeedbackOnboardingIntent? = null
        composeRule.setContent {
            HilitTheme {
                FeedbackOnboardingContent(
                    state =
                        FeedbackOnboardingState(
                            requesterName = "합성 요청자",
                            loadState = FeedbackOnboardingLoadState.Ready,
                        ),
                    onIntent = { receivedIntent = it },
                )
            }
        }

        composeRule.onNodeWithText("피드백 시작하기").performClick()

        assertEquals(FeedbackOnboardingIntent.StartClicked, receivedIntent)
    }

    @Test
    fun `서버가 지정한 축만 순서대로 표시하고 선택을 전달한다`() {
        val received = mutableListOf<FeedbackIntent>()
        composeRule.setContent {
            HilitTheme {
                FeedbackContent(
                    state =
                        FeedbackState(
                            requesterName = "합성 요청자",
                            videoUrl = "https://example.invalid/synthetic.mp4",
                            axes =
                                listOf(
                                    FeedbackAxisUiModel(GuestFeedbackAxisCode.GAZE, "시선"),
                                    FeedbackAxisUiModel(GuestFeedbackAxisCode.VOICE, "목소리"),
                                ),
                            hasLoaded = true,
                            isVideoIntroVisible = false,
                        ),
                    onIntent = received::add,
                    showVideoPlayer = false,
                )
            }
        }

        composeRule.onAllNodesWithText("시선").assertCountEquals(1)
        composeRule.onAllNodesWithText("목소리").assertCountEquals(1)
        composeRule.onNodeWithText("목소리").performClick()

        assertEquals(FeedbackIntent.AxisSelected(GuestFeedbackAxisCode.VOICE), received.last())
    }

    @Test
    fun `선택한 축의 질문과 평가 선택지를 표시하고 입력을 전달한다`() {
        val received = mutableListOf<FeedbackIntent>()
        composeRule.setContent {
            HilitTheme {
                FeedbackContent(
                    state = selectedState(),
                    onIntent = received::add,
                    showVideoPlayer = false,
                )
            }
        }

        composeRule.onNodeWithText("합성 요청자님은 눈을 잘 마주치나요?").assertIsDisplayed()
        composeRule.onNodeWithText("잘 맞춤").performClick()

        assertEquals(FeedbackIntent.RatingSelected(GuestFeedbackAxisCode.GAZE, 4), received.last())
    }

    @Test
    fun `코멘트 유무에 따라 선택 버튼과 수정 동작을 구분한다`() {
        val received = mutableListOf<FeedbackIntent>()
        composeRule.setContent {
            HilitTheme {
                FeedbackContent(
                    state = selectedState(comment = "합성 코멘트"),
                    onIntent = received::add,
                    showVideoPlayer = false,
                )
            }
        }

        composeRule.onNodeWithText("합성 코멘트").assertIsDisplayed()
        composeRule.onNodeWithText("수정").performClick()

        assertEquals(
            FeedbackIntent.CommentEditorClicked(GuestFeedbackAxisCode.GAZE),
            received.last(),
        )
    }

    @Test
    fun `모든 축 평가가 끝나야 피드백 종료와 확대 동작을 전달한다`() {
        val received = mutableListOf<FeedbackIntent>()
        var state by mutableStateOf(selectedState())
        composeRule.setContent {
            HilitTheme {
                FeedbackContent(state = state, onIntent = received::add, showVideoPlayer = false)
            }
        }
        composeRule.onNodeWithText("피드백 종료하기").assertIsNotEnabled()

        composeRule.runOnIdle { state = selectedState(level = 4) }
        composeRule.onNodeWithText("피드백 종료하기").assertIsEnabled().performClick()
        assertEquals(FeedbackIntent.ReviewClicked, received.last())

        composeRule.onNodeWithContentDescription("영상 확대").performClick()
        assertEquals(FeedbackIntent.VideoExpanded, received.last())
    }

    @Test
    fun `코멘트 입력은 백 자까지만 전달하고 다음과 닫기 동작을 구분한다`() {
        var value = ""
        var confirmCount = 0
        var dismissCount = 0
        composeRule.setContent {
            HilitTheme {
                GuestFeedbackCommentModal(
                    value = value,
                    onValueChange = { value = it },
                    onConfirm = { confirmCount += 1 },
                    onDismiss = { dismissCount += 1 },
                )
            }
        }

        composeRule.onNodeWithText("선택 사항이에요").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("선택 코멘트").performTextInput("가".repeat(101))
        composeRule.onNodeWithText("다음").performClick()

        assertEquals(100, value.length)
        assertEquals(1, confirmCount)
        assertEquals(0, dismissCount)
    }

    @Test
    fun `제출 중 검토 화면은 영상 다시보기와 코멘트 수정을 전달하지 않는다`() {
        val received = mutableListOf<FeedbackReviewIntent>()
        composeRule.setContent {
            HilitTheme {
                FeedbackReviewContent(
                    state = submittingReviewState(),
                    onIntent = received::add,
                )
            }
        }

        composeRule.onNodeWithText("영상 다시보기").assertIsNotEnabled().performClick()
        composeRule
            .onNodeWithContentDescription("시선 코멘트 수정")
            .assertIsNotEnabled()
            .performTouchInput { click() }

        assertTrue(received.isEmpty())
    }

    @Test
    fun `제출 중 검토 화면은 시스템 뒤로 가기를 소비하고 이동을 요청하지 않는다`() {
        var replayCount = 0
        composeRule.setContent {
            FeedbackReviewBackHandler(
                isSubmitting = true,
                onReplayRequested = { replayCount++ },
            )
        }

        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()

        assertEquals(0, replayCount)
    }

    private fun selectedState(
        level: Int? = null,
        comment: String = "",
    ) = FeedbackState(
        requesterName = "합성 요청자",
        videoUrl = "https://example.invalid/synthetic.mp4",
        axes =
            listOf(
                FeedbackAxisUiModel(
                    code = GuestFeedbackAxisCode.GAZE,
                    title = "시선",
                    level = level,
                    comment = comment,
                ),
            ),
        selectedAxis = GuestFeedbackAxisCode.GAZE,
        isVideoExpanded = false,
        isVideoIntroVisible = false,
        hasLoaded = true,
    )

    private fun submittingReviewState() =
        FeedbackReviewState(
            requesterName = "합성 요청자",
            nickname = "합성 지인",
            axes =
                listOf(
                    FeedbackReviewAxisUiModel(
                        code = GuestFeedbackAxisCode.GAZE,
                        title = "시선",
                        level = 4,
                        levelLabel = "잘 맞춤",
                        comment = "합성 코멘트",
                    ),
                ),
            isSubmitting = true,
            hasLoaded = true,
        )
}

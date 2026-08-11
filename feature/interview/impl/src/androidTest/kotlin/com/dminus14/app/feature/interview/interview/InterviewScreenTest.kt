package com.dminus14.app.feature.interview.interview

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dminus14.designsystem.theme.HilitTheme
import org.junit.Rule
import org.junit.Test

class InterviewScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `시작 안내 단계에서 시작 버튼을 표시한다`() {
        setContent(
            InterviewState(
                screenState = InterviewScreenState.START_GUIDE,
                isCameraPermissionGranted = true,
                isCameraReady = true,
                isMicrophoneReady = true,
                isServerReady = true,
                hasEnoughStorage = true,
            ),
        )

        composeRule.onNodeWithText("면접 시작하기").assertIsDisplayed()
    }

    @Test
    fun `답변 제출 단계에서 정리 문구를 표시한다`() {
        setContent(InterviewState(screenState = InterviewScreenState.ANSWER_SUBMITTING))

        composeRule.onNodeWithText("답변을 정리하고 있어요").assertIsDisplayed()
    }

    @Test
    fun `마무리 단계에서 업로드 보존 안내를 표시한다`() {
        setContent(InterviewState(screenState = InterviewScreenState.FINISHING))

        composeRule.onNodeWithText("24시간 안에", substring = true).assertIsDisplayed()
    }

    private fun setContent(state: InterviewState) {
        composeRule.setContent {
            HilitTheme { InterviewContent(state = state, onIntent = {}) }
        }
    }
}

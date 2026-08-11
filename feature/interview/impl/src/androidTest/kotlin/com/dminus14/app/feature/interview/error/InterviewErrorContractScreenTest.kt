package com.dminus14.app.feature.interview.error

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.dminus14.app.feature.interview.api.InterviewErrorType
import com.dminus14.designsystem.theme.HilitTheme
import org.junit.Rule
import org.junit.Test

class InterviewErrorContractScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `마이크 오류를 정적 화면으로 표시한다`() {
        assertErrorContent(InterviewErrorType.MIC_DEVICE, "마이크 상태를 확인하고")
    }

    @Test
    fun `네트워크 오류를 정적 화면으로 표시한다`() {
        assertErrorContent(InterviewErrorType.NETWORK, "네트워크가 불안정해")
    }

    @Test
    fun `STT 오류를 정적 화면으로 표시한다`() {
        assertErrorContent(InterviewErrorType.STT, "답변을 정상적으로 인식하지 못해")
    }

    @Test
    fun `서버 임시 오류를 정적 화면으로 표시한다`() {
        assertErrorContent(InterviewErrorType.SERVER_TEMPORARY, "답변을 처리하지 못했어요")
    }

    @Test
    fun `서버 확인 중에는 로딩 상태를 표시한다`() {
        composeRule.setContent {
            HilitTheme {
                InterviewErrorContent(
                    state =
                        InterviewErrorState(
                            errorType = InterviewErrorType.NETWORK,
                            isLoading = true,
                        ),
                    onIntent = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("면접 상태 확인 중").assertIsDisplayed()
    }

    private fun assertErrorContent(
        errorType: InterviewErrorType,
        expectedText: String,
    ) {
        composeRule.setContent {
            HilitTheme {
                InterviewErrorContent(
                    state = InterviewErrorState(errorType),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText(expectedText, substring = true).assertIsDisplayed()
    }
}

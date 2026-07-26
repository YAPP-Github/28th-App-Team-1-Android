package com.dminus14.app.dialog

import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.dminus14.designsystem.component.modal.DMinusModal
import com.dminus14.designsystem.theme.HilitTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DMinusDialogTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `다이얼로그는 콘텐츠를 표시하고 버튼 콜백을 호출한다`() {
        var confirmCount = 0
        var cancelCount = 0
        composeRule.setContent {
            HilitTheme {
                DMinusModal(
                    title = "Synthetic title",
                    message = "Synthetic message",
                    confirmText = "Confirm",
                    cancelText = "Cancel",
                    onConfirm = { confirmCount++ },
                    onCancel = { cancelCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Synthetic title").assertIsDisplayed()
        composeRule.onNodeWithText("Synthetic message").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm").performClick()
        composeRule.onNodeWithText("Cancel").performClick()

        assertEquals(1, confirmCount)
        assertEquals(1, cancelCount)
    }

    @Test
    fun `닫을 수 있는 다이얼로그는 뒤로 가기 시 닫기 콜백을 호출한다`() {
        var dismissCount = 0
        setDialog(dismissible = true, onDismiss = { dismissCount++ })

        pressSystemBack()
        composeRule.waitForIdle()

        assertEquals(1, dismissCount)
    }

    @Test
    fun `닫을 수 없는 다이얼로그는 뒤로 가기를 무시한다`() {
        var dismissCount = 0
        setDialog(dismissible = false, onDismiss = { dismissCount++ })

        pressSystemBack()
        composeRule.waitForIdle()

        assertEquals(0, dismissCount)
        composeRule.onNodeWithText("Synthetic message").assertIsDisplayed()
    }

    @Test
    fun `닫을 수 있는 다이얼로그는 바깥 영역 터치 시 닫기 콜백을 호출한다`() {
        var dismissCount = 0
        setDialog(dismissible = true, onDismiss = { dismissCount++ })

        tapOutsideDialog()
        composeRule.waitForIdle()

        assertEquals(1, dismissCount)
    }

    @Test
    fun `닫을 수 없는 다이얼로그는 바깥 영역 터치를 무시한다`() {
        var dismissCount = 0
        setDialog(dismissible = false, onDismiss = { dismissCount++ })

        tapOutsideDialog()
        composeRule.waitForIdle()

        assertEquals(0, dismissCount)
        composeRule.onNodeWithText("Synthetic message").assertIsDisplayed()
    }

    private fun tapOutsideDialog() {
        val decorView = composeRule.activity.window.decorView
        val decorLocation = IntArray(2)
        composeRule.runOnUiThread { decorView.getLocationOnScreen(decorLocation) }
        tapScreen(
            Offset(
                x = decorLocation[0] + decorView.width * 0.05f,
                y = decorLocation[1] + decorView.height * 0.5f,
            ),
        )
    }

    private fun pressSystemBack() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val downTime = SystemClock.uptimeMillis()
        check(
            instrumentation.uiAutomation.injectInputEvent(
                KeyEvent(
                    downTime,
                    downTime,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_BACK,
                    0,
                ),
                true,
            ),
        )
        SystemClock.sleep(50)
        check(
            instrumentation.uiAutomation.injectInputEvent(
                KeyEvent(
                    downTime,
                    SystemClock.uptimeMillis(),
                    KeyEvent.ACTION_UP,
                    KeyEvent.KEYCODE_BACK,
                    0,
                ),
                true,
            ),
        )
    }

    private fun tapScreen(position: Offset) {
        val downTime = SystemClock.uptimeMillis()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val downEvent =
            MotionEvent
                .obtain(
                    downTime,
                    downTime,
                    MotionEvent.ACTION_DOWN,
                    position.x,
                    position.y,
                    0,
                ).apply { source = InputDevice.SOURCE_TOUCHSCREEN }
        val upEvent =
            MotionEvent
                .obtain(
                    downTime,
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    position.x,
                    position.y,
                    0,
                ).apply { source = InputDevice.SOURCE_TOUCHSCREEN }

        try {
            check(instrumentation.uiAutomation.injectInputEvent(downEvent, true))
            SystemClock.sleep(50)
            check(instrumentation.uiAutomation.injectInputEvent(upEvent, true))
        } finally {
            downEvent.recycle()
            upEvent.recycle()
        }
    }

    private fun setDialog(
        dismissible: Boolean,
        onDismiss: () -> Unit,
    ) {
        composeRule.setContent {
            HilitTheme {
                DMinusModal(
                    title = "Synthetic title",
                    message = "Synthetic message",
                    confirmText = "Confirm",
                    dismissible = dismissible,
                    onConfirm = {},
                    onDismiss = onDismiss,
                )
            }
        }
    }
}

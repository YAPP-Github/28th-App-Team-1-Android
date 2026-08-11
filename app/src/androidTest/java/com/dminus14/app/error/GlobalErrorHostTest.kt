package com.dminus14.app.error

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GlobalErrorHostTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `delivery ID가 있는 전역 오류만 표시 확인을 전달한다`() {
        var acknowledgedId: String? = null
        composeRule.setContent {
            HilitTheme {
                GlobalErrorHost(
                    onExit = {},
                    onGlobalEventRendered = { acknowledgedId = it },
                )
            }
        }
        composeRule.waitForIdle()

        runBlocking {
            GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError, "synthetic-delivery")
        }

        composeRule.waitUntil { acknowledgedId != null }
        assertEquals("synthetic-delivery", acknowledgedId)
    }
}

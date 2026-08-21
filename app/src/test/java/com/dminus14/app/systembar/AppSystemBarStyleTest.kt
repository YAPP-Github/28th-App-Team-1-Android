package com.dminus14.app.systembar

import androidx.compose.ui.graphics.Color
import com.dminus14.app.feature.interview.api.InterviewRoute
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import com.dminus14.app.feature.interviewreport.api.InterviewReportPlayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSystemBarStyleTest {
    @Test
    fun `일반 화면은 기본 배경과 어두운 아이콘을 사용한다`() {
        val defaultColor = Color.White

        val style = resolveAppSystemBarStyle(route = Any(), defaultBarColor = defaultColor)

        assertEquals(defaultColor, style.statusBarColor)
        assertEquals(defaultColor, style.navigationBarColor)
        assertTrue(style.useDarkStatusBarIcons)
        assertTrue(style.useDarkNavigationBarIcons)
        assertFalse(style.drawsBehindSystemBars)
    }

    @Test
    fun `면접 화면은 투명 시스템 바와 밝은 아이콘을 사용한다`() {
        val style = resolveAppSystemBarStyle(route = InterviewRoute, defaultBarColor = Color.White)

        assertNull(style.statusBarColor)
        assertNull(style.navigationBarColor)
        assertFalse(style.useDarkStatusBarIcons)
        assertFalse(style.useDarkNavigationBarIcons)
        assertTrue(style.drawsBehindSystemBars)
    }

    @Test
    fun `리포트 화면은 기본 시스템 바를 유지하며 전체 화면으로 그린다`() {
        val routes = listOf(InterviewReport(sessionId = 1L), InterviewReportPlayer(1L, null))

        routes.forEach { route ->
            val style = resolveAppSystemBarStyle(route = route, defaultBarColor = Color.White)

            assertEquals(Color.White, style.statusBarColor)
            assertEquals(Color.White, style.navigationBarColor)
            assertTrue(style.useDarkStatusBarIcons)
            assertTrue(style.useDarkNavigationBarIcons)
            assertTrue(style.drawsBehindSystemBars)
        }
    }
}

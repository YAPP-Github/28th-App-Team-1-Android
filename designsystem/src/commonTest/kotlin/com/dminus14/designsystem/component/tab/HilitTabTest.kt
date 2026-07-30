package com.dminus14.designsystem.component.tab

import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.DefaultHilitColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HilitTabTest {
    private val colors = DefaultHilitColors

    @Test
    fun `선택 상태는 Black 800 내용과 하단선을 표시한다`() {
        val style = hilitTabStyle(selected = true, enabled = true, colors = colors)

        assertEquals(colors.hilitBlack800, style.contentColor)
        assertEquals(colors.hilitBlack800, style.indicatorColor)
        assertEquals(1.5.dp, style.indicatorWidth)
        assertTrue(style.showIndicator)
    }

    @Test
    fun `기본 상태는 Black 800 내용을 표시하고 하단선을 숨긴다`() {
        val style = hilitTabStyle(selected = false, enabled = true, colors = colors)

        assertEquals(colors.hilitBlack800, style.contentColor)
        assertFalse(style.showIndicator)
    }

    @Test
    fun `비활성 상태는 Gray 500 내용을 표시하고 하단선을 숨긴다`() {
        val style = hilitTabStyle(selected = true, enabled = false, colors = colors)

        assertEquals(colors.gray500, style.contentColor)
        assertFalse(style.showIndicator)
    }
}

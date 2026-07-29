package com.dminus14.designsystem.component.toggle

import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.DefaultHilitColors
import kotlin.test.Test
import kotlin.test.assertEquals

class HilitToggleTest {
    private val colors = DefaultHilitColors

    @Test
    fun `켜진 상태는 Green 500 손잡이를 오른쪽에 배치한다`() {
        val style = hilitToggleStyle(checked = true, colors = colors)

        assertEquals(colors.gray900, style.trackColor)
        assertEquals(colors.hilitGreen500, style.thumbColor)
        assertEquals(22.dp, style.thumbOffset)
        assertEquals(200, TOGGLE_ANIMATION_DURATION_MILLIS)
    }

    @Test
    fun `꺼진 상태는 Gray 50 손잡이를 왼쪽에 배치한다`() {
        val style = hilitToggleStyle(checked = false, colors = colors)

        assertEquals(colors.gray900, style.trackColor)
        assertEquals(colors.gray50, style.thumbColor)
        assertEquals(0.dp, style.thumbOffset)
    }
}

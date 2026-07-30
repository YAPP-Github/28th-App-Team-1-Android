package com.dminus14.designsystem.component.button

import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.DefaultHilitColors
import kotlin.test.Test
import kotlin.test.assertEquals

class HilitMiniButtonTest {
    private val colors = DefaultHilitColors

    @Test
    fun `Light는 Gray 100 배경과 Black 800 내용을 사용한다`() {
        val style = hilitMiniButtonStyle(HilitMiniButtonColor.Light, colors)

        assertEquals(colors.gray100, style.backgroundColor)
        assertEquals(colors.hilitBlack800, style.contentColor)
        assertEquals(8.dp, style.contentPadding)
        assertEquals(8.dp, style.contentSpacing)
    }

    @Test
    fun `Dark는 Gray 900 배경과 흰 내용을 사용한다`() {
        val style = hilitMiniButtonStyle(HilitMiniButtonColor.Dark, colors)

        assertEquals(colors.gray900, style.backgroundColor)
        assertEquals(colors.hilitWhite, style.contentColor)
        assertEquals(8.dp, style.contentPadding)
        assertEquals(8.dp, style.contentSpacing)
    }
}

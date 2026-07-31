package com.dminus14.designsystem.component.button

import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.DefaultHilitColors
import kotlin.test.Test
import kotlin.test.assertEquals

class HilitOptionalButtonTest {
    @Test
    fun `Optional 버튼은 Figma 외곽선과 내부 배치 수치를 사용한다`() {
        val colors = DefaultHilitColors
        val style = hilitOptionalButtonStyle(colors)

        assertEquals(colors.hilitWhite, style.backgroundColor)
        assertEquals(colors.gray900, style.contentColor)
        assertEquals(colors.gray100, style.outlineColor)
        assertEquals(1.2.dp, style.outlineWidth)
        assertEquals(4.dp, style.dashLength)
        assertEquals(4.dp, style.dashGap)
        assertEquals(42.dp, style.minHeight)
        assertEquals(12.dp, style.contentPadding)
        assertEquals(8.dp, style.contentSpacing)
    }
}

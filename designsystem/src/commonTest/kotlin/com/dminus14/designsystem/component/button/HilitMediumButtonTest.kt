package com.dminus14.designsystem.component.button

import com.dminus14.designsystem.theme.DefaultHilitColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HilitMediumButtonTest {
    private val colors = DefaultHilitColors

    @Test
    fun `Default는 흰 배경과 Gray 200 외곽선을 사용한다`() {
        val style = enabledStyle(HilitMediumButtonColor.Default)

        assertEquals(colors.hilitWhite, style.backgroundColor)
        assertEquals(colors.hilitBlack800, style.contentColor)
        assertEquals(colors.gray200, style.outlineColor)
        assertEquals(HilitMediumButtonTypography.Body3, style.typography)
    }

    @Test
    fun `Gray는 흰 배경과 Gray 100 외곽선을 사용한다`() {
        val style = enabledStyle(HilitMediumButtonColor.Gray)

        assertEquals(colors.hilitWhite, style.backgroundColor)
        assertEquals(colors.gray700, style.contentColor)
        assertEquals(colors.gray100, style.outlineColor)
        assertEquals(HilitMediumButtonTypography.Body3, style.typography)
    }

    @Test
    fun `Blue는 Positive 색상과 Body 2를 사용한다`() {
        val style = enabledStyle(HilitMediumButtonColor.Blue)

        assertEquals(colors.positive200, style.backgroundColor)
        assertEquals(colors.positive800, style.contentColor)
        assertEquals(colors.positive500, style.outlineColor)
        assertEquals(HilitMediumButtonTypography.Body2, style.typography)
    }

    @Test
    fun `Red는 Error 색상과 Body 2를 사용한다`() {
        val style = enabledStyle(HilitMediumButtonColor.Red)

        assertEquals(colors.error200, style.backgroundColor)
        assertEquals(colors.error500, style.contentColor)
        assertEquals(colors.error500, style.outlineColor)
        assertEquals(HilitMediumButtonTypography.Body2, style.typography)
    }

    @Test
    fun `Green은 Hilit Green 색상과 Body 3을 사용한다`() {
        val style = enabledStyle(HilitMediumButtonColor.Green)

        assertEquals(colors.hilitGreen500, style.backgroundColor)
        assertEquals(colors.hilitGreen800, style.contentColor)
        assertEquals(colors.hilitGreen600, style.outlineColor)
        assertEquals(HilitMediumButtonTypography.Body3, style.typography)
    }

    @Test
    fun `Black은 외곽선 없이 Black 800 배경을 사용한다`() {
        val style = enabledStyle(HilitMediumButtonColor.Black)

        assertEquals(colors.hilitBlack800, style.backgroundColor)
        assertEquals(colors.hilitWhite, style.contentColor)
        assertNull(style.outlineColor)
        assertEquals(HilitMediumButtonTypography.Body3, style.typography)
    }

    @Test
    fun `비활성 상태는 선택 색상과 관계없이 공통 Disabled 색상을 사용한다`() {
        HilitMediumButtonColor.entries.forEach { color ->
            val style = hilitMediumButtonStyle(color = color, enabled = false, colors = colors)

            assertEquals(colors.gray50, style.backgroundColor)
            assertEquals(colors.gray300, style.contentColor)
            assertEquals(colors.gray300, style.outlineColor)
            assertEquals(HilitMediumButtonTypography.Body3, style.typography)
        }
    }

    private fun enabledStyle(color: HilitMediumButtonColor): HilitMediumButtonStyle =
        hilitMediumButtonStyle(color = color, enabled = true, colors = colors)
}

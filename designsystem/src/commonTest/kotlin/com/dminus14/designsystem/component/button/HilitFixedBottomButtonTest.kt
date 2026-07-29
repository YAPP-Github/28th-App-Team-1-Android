package com.dminus14.designsystem.component.button

import com.dminus14.designsystem.theme.DefaultHilitColors
import kotlin.test.Test
import kotlin.test.assertEquals

class HilitFixedBottomButtonTest {
    private val colors = DefaultHilitColors

    @Test
    fun `라이트 모드의 활성 버튼은 검정 배경과 흰 글자를 사용한다`() {
        val color = buttonColors(type = HilitButtonType.Light, enabled = true)

        assertEquals(colors.hilitBlack800, color.backgroundColor)
        assertEquals(colors.hilitWhite, color.contentColor)
        assertEquals(colors.gray900, color.pressColor)
    }

    @Test
    fun `다크 모드의 활성 버튼은 흰 배경과 검정 글자를 사용한다`() {
        val color = buttonColors(type = HilitButtonType.Dark, enabled = true)

        assertEquals(colors.hilitWhite, color.backgroundColor)
        assertEquals(colors.hilitBlack800, color.contentColor)
        assertEquals(colors.gray100, color.pressColor)
    }

    @Test
    fun `라이트 모드의 비활성 버튼은 회색 배경과 회색 글자를 사용한다`() {
        val color = buttonColors(type = HilitButtonType.Light, enabled = false)

        assertEquals(colors.gray50, color.backgroundColor)
        assertEquals(colors.gray300, color.contentColor)
    }

    @Test
    fun `다크 모드의 비활성 버튼은 흰 배경과 회색 글자를 사용한다`() {
        val color = buttonColors(type = HilitButtonType.Dark, enabled = false)

        assertEquals(colors.hilitWhite, color.backgroundColor)
        assertEquals(colors.gray300, color.contentColor)
    }

    private fun buttonColors(
        type: HilitButtonType,
        enabled: Boolean,
    ): BtnColorSet = hilitFixedBottomButtonColors(type = type, enabled = enabled, colors = colors)
}

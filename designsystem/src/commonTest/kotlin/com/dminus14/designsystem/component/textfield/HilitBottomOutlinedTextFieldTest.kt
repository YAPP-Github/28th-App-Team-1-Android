package com.dminus14.designsystem.component.textfield

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HilitBottomOutlinedTextFieldTest {
    @Test
    fun `입력값이 비어 있으면 Placeholder를 너비 측정에 사용한다`() {
        assertEquals(
            expected = "이름을 알려주세요",
            actual =
                bottomOutlinedTextFieldMeasurementText(
                    value = "",
                    placeholder = "이름을 알려주세요",
                ),
        )
    }

    @Test
    fun `입력값이 있으면 Placeholder 대신 입력값을 너비 측정에 사용한다`() {
        assertEquals(
            expected = "박민",
            actual =
                bottomOutlinedTextFieldMeasurementText(
                    value = "박민",
                    placeholder = "이름을 알려주세요",
                ),
        )
    }

    @Test
    fun `공백 입력값은 비어 있는 값으로 정규화하지 않는다`() {
        assertEquals(
            expected = " ",
            actual =
                bottomOutlinedTextFieldMeasurementText(
                    value = " ",
                    placeholder = "Placeholder",
                ),
        )
    }

    @Test
    fun `입력값과 Placeholder가 모두 비면 한글 한 글자를 너비 측정에 사용한다`() {
        assertEquals(
            expected = HILIT_BOTTOM_OUTLINED_MINIMUM_WIDTH_TEXT,
            actual = bottomOutlinedTextFieldMeasurementText(value = "", placeholder = ""),
        )
    }

    @Test
    fun `포커스가 없어도 입력값이 있으면 활성 상태이다`() {
        assertTrue(isBottomOutlinedTextFieldActive(isFocused = false, value = "박민"))
    }

    @Test
    fun `입력값이 없어도 포커스가 있으면 활성 상태이다`() {
        assertTrue(isBottomOutlinedTextFieldActive(isFocused = true, value = ""))
    }

    @Test
    fun `입력값과 포커스가 모두 없을 때만 비활성 상태이다`() {
        assertFalse(isBottomOutlinedTextFieldActive(isFocused = false, value = ""))
    }

    @Test
    fun `소수 픽셀 너비는 글리프가 잘리지 않도록 올림한다`() {
        assertEquals(expected = 13, actual = ceilTextWidthInPixels(12.01f))
        assertEquals(expected = 12, actual = ceilTextWidthInPixels(12f))
    }
}

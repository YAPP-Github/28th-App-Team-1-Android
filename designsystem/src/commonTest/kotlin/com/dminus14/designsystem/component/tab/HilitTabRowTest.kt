package com.dminus14.designsystem.component.tab

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HilitTabRowTest {
    @Test
    fun `항목 수에 따라 총 52dp를 동일한 간격으로 나눈다`() {
        assertEquals(52.dp, hilitTabRowGap(itemCount = 2))
        assertEquals(26.dp, hilitTabRowGap(itemCount = 3))
        assertEquals((52f / 3f).dp, hilitTabRowGap(itemCount = 4))
        assertEquals(13.dp, hilitTabRowGap(itemCount = 5))
    }

    @Test
    fun `항목 수가 2개보다 적거나 5개보다 많으면 거부한다`() {
        assertFailsWith<IllegalArgumentException> { hilitTabRowGap(itemCount = 1) }
        assertFailsWith<IllegalArgumentException> { hilitTabRowGap(itemCount = 6) }
    }

    @Test
    fun `선택 index가 항목 범위를 벗어나면 거부한다`() {
        val items = listOf(HilitTabItem("첫 번째"), HilitTabItem("두 번째"))

        assertFailsWith<IllegalArgumentException> {
            validateHilitTabRow(items = items, selectedIndex = 2)
        }
    }

    @Test
    fun `비활성 항목을 선택하면 거부한다`() {
        val items =
            listOf(
                HilitTabItem(text = "첫 번째"),
                HilitTabItem(text = "두 번째", enabled = false),
            )

        assertFailsWith<IllegalArgumentException> {
            validateHilitTabRow(items = items, selectedIndex = 1)
        }
    }

    @Test
    fun `2개부터 5개 사이의 활성 항목과 유효한 선택은 허용한다`() {
        (2..5).forEach { itemCount ->
            val items = List(itemCount) { index -> HilitTabItem(text = "$index") }

            validateHilitTabRow(items = items, selectedIndex = itemCount - 1)
        }
    }
}

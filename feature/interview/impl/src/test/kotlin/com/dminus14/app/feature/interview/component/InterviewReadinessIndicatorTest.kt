package com.dminus14.app.feature.interview.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewReadinessIndicatorTest {
    @Test
    fun `넓은 화면에서는 빈 구간이 없도록 문자열 개수를 늘린다`() {
        val rowWidth = 1_000
        val textStride = 200

        val itemCount = calculateMarqueeItemCount(rowWidth, textStride)

        assertEquals(7, itemCount)
        assertTrue((itemCount - 2) * textStride >= rowWidth)
    }

    @Test
    fun `좁은 화면에서도 기존 세 문자열은 유지한다`() {
        val itemCount =
            calculateMarqueeItemCount(
                rowWidth = 80,
                textStride = 200,
            )

        assertEquals(3, itemCount)
    }
}

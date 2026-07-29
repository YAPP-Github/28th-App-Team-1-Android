package com.dminus14.designsystem.component.loading

import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HilitLoadingIndicatorTest {
    @Test
    fun `기본 크기는 74dp 컨테이너 안에 72dp 곱하기 73dp 그래픽을 배치한다`() {
        val geometry = hilitLoadingIndicatorGeometry(size = 74.dp)

        assertEquals(74.dp, geometry.containerSize)
        assertEquals(72.dp, geometry.graphicWidth)
        assertEquals(73.dp, geometry.graphicHeight)
    }

    @Test
    fun `사용자 크기에서도 원본 그래픽 비율을 유지한다`() {
        val geometry = hilitLoadingIndicatorGeometry(size = 148.dp)

        assertEquals(148.dp, geometry.containerSize)
        assertEquals(144.dp, geometry.graphicWidth)
        assertEquals(146.dp, geometry.graphicHeight)
    }

    @Test
    fun `0 이하 크기는 거부한다`() {
        assertFailsWith<IllegalArgumentException> {
            hilitLoadingIndicatorGeometry(size = 0.dp)
        }
    }

    @Test
    fun `시계 방향으로 1점5초에 한 바퀴 회전한다`() {
        assertEquals(1_500, LOADING_INDICATOR_DURATION_MILLIS)
        assertEquals(0f, LOADING_INDICATOR_START_DEGREES)
        assertEquals(360f, LOADING_INDICATOR_END_DEGREES)
    }

    @Test
    fun `회전 곡선은 모든 구간에서 멈추지 않고 앞으로 진행한다`() {
        val progress =
            (0..100).map { step ->
                LOADING_INDICATOR_EASING.transform(step / 100f)
            }

        progress.zipWithNext().forEach { (current, next) ->
            assertTrue(next > current)
        }
    }

    @Test
    fun `회전 곡선은 시작과 끝보다 중간 구간에서 빠르게 진행한다`() {
        val firstQuarter = LOADING_INDICATOR_EASING.transform(0.25f)
        val middleQuarter =
            LOADING_INDICATOR_EASING.transform(0.5f) - firstQuarter
        val lastQuarter =
            1f - LOADING_INDICATOR_EASING.transform(0.75f)

        assertTrue(middleQuarter > firstQuarter)
        assertTrue(middleQuarter > lastQuarter)
        assertTrue(abs(firstQuarter - lastQuarter) < 0.0001f)
    }
}

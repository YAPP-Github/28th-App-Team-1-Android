package com.dminus14.designsystem.component.text

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.buildAnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HilitTextHighlightTest {
    @Test
    fun `하이라이트 주석은 작성한 문자열 범위만 기록한다`() {
        val text =
            buildAnnotatedString {
                append("앞")
                withHilitTextHighlight {
                    append("강조")
                }
                append("뒤")
            }

        assertEquals(
            listOf(HilitTextRange(start = 1, end = 3)),
            normalizedHighlightRanges(text),
        )
        assertTrue(text.spanStyles.isEmpty())
    }

    @Test
    fun `빈 하이라이트 범위는 무시한다`() {
        val text =
            buildAnnotatedString {
                append("앞")
                withHilitTextHighlight {}
                append("뒤")
            }

        assertTrue(normalizedHighlightRanges(text).isEmpty())
    }

    @Test
    fun `인접한 하이라이트 범위는 하나로 합친다`() {
        val text =
            buildAnnotatedString {
                withHilitTextHighlight {
                    append("첫째")
                }
                withHilitTextHighlight {
                    append("둘째")
                }
            }

        assertEquals(
            listOf(HilitTextRange(start = 0, end = 4)),
            normalizedHighlightRanges(text),
        )
    }

    @Test
    fun `플레이스홀더 너비는 텍스트보다 좌우 여백만큼 넓다`() {
        assertEquals(
            expected = 116f,
            actual =
                highlightPlaceholderWidth(
                    textWidthPx = 100f,
                    horizontalPaddingPx = 8f,
                ),
        )
    }

    @Test
    fun `사다리꼴 꼭짓점은 피그마의 기울기 방향을 따른다`() {
        assertEquals(
            listOf(
                Offset(x = 4f, y = 0f),
                Offset(x = 78f, y = 0f),
                Offset(x = 74f, y = 27f),
                Offset(x = 0f, y = 27f),
            ),
            hilitTrapezoidVertices(
                width = 78f,
                height = 27f,
                slant = 4f,
            ),
        )
    }
}

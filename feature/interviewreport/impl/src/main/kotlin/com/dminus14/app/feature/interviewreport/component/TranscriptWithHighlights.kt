package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.app.feature.interviewreport.model.HighlightUiModel
import com.dminus14.app.feature.interviewreport.model.HighlightUiTone
import com.dminus14.designsystem.theme.HilitTheme

private const val HIGHLIGHT_ANNOTATION_TAG = "highlight"
private const val HIGHLIGHT_BACKGROUND_ALPHA = 0.6f

/**
 * 답변 대본 위에 하이라이트 span 을 강조 렌더한다.
 *
 * - 하이라이트가 겹치거나 순서가 뒤엉킨 경우에도 안전하도록 시작 인덱스 기준 정렬 후
 *   앞선 span 이 끝난 위치부터만 다음 span 을 반영한다.
 * - 각 하이라이트 구간에 [HIGHLIGHT_ANNOTATION_TAG] 로 annotation 을 붙여서
 *   탭 시 인덱스를 상위 콜백에 넘긴다. 해상도 낮음 카드처럼 하이라이트가 없으면
 *   순수 텍스트로 렌더되어 탭에도 반응하지 않는다 (기획서 §3-1).
 */
@Composable
internal fun TranscriptWithHighlights(
    card: CardUiModel,
    onHighlightClick: (spanIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    val annotated =
        buildAnnotatedString {
            val textLength = card.transcript.length
            var cursor = 0
            card.highlights
                .withIndex()
                .sortedBy { (_, span) -> span.startIndex }
                .forEach { (index, span) ->
                    // span.startIndex/endIndex 는 서버 응답 값이라 transcript 길이를 넘거나
                    // start > end 로 어긋날 수 있다. HighlightDetailBottomSheet 의 safeSlice 와
                    // 같은 방식으로 클램프해 substring 의 StringIndexOutOfBoundsException(리포트
                    // 화면 전체 크래시)을 막는다.
                    val start = span.startIndex.coerceIn(0, textLength)
                    val end = span.endIndex.coerceIn(start, textLength)
                    if (start >= cursor && end > start) {
                        if (start > cursor) {
                            append(card.transcript.safeSlice(cursor, start))
                        }
                        val style = span.tone.toSpanStyle(colors)
                        pushStringAnnotation(HIGHLIGHT_ANNOTATION_TAG, index.toString())
                        withStyle(style) {
                            append(card.transcript.safeSlice(start, end))
                        }
                        pop()
                        cursor = end
                    }
                }
            if (cursor < textLength) {
                append(card.transcript.safeSlice(cursor, textLength))
            }
        }

    ClickableText(
        text = annotated,
        style = HilitTheme.typography.body3.copy(color = colors.gray50),
        modifier = modifier,
        onClick = { offset ->
            annotated
                .getStringAnnotations(HIGHLIGHT_ANNOTATION_TAG, offset, offset)
                .firstOrNull()
                ?.item
                ?.toIntOrNull()
                ?.let(onHighlightClick)
        },
    )
}

/**
 * 다크 리포트 대본 하이라이트 색.
 * - GOOD(잘함) → positive500 (cyan #00CFEF)
 * - IMPROVE(개선) → error400 (red #FF8383)
 * - 그 외(UNKNOWN 등) → 기본 대본색 유지, 배경 없음
 * - 배경은 gray800(#31333B) 60% 알파.
 */
private fun HighlightUiTone.toSpanStyle(
    colors: com.dminus14.designsystem.theme.HilitColors,
): SpanStyle {
    val foreground =
        when (this) {
            HighlightUiTone.POSITIVE -> colors.positive500
            HighlightUiTone.NEGATIVE -> colors.error400
            HighlightUiTone.NEUTRAL -> colors.gray50
        }
    val background =
        if (this == HighlightUiTone.NEUTRAL) {
            Color.Transparent
        } else {
            colors.gray800.copy(alpha = HIGHLIGHT_BACKGROUND_ALPHA)
        }
    return SpanStyle(
        background = background,
        color = foreground,
        fontWeight =
            if (this == HighlightUiTone.NEUTRAL) FontWeight.Normal else FontWeight.SemiBold,
    )
}

/** 텍스트 없이 span 만 있는 경우를 대비한 폴백. 하이라이트 없이 순수 대본을 렌더한다. */
@Composable
internal fun PlainTranscript(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    ClickableText(
        text = AnnotatedString(text),
        style = HilitTheme.typography.body3.copy(color = colors.gray50),
        modifier = modifier,
        onClick = {},
    )
}

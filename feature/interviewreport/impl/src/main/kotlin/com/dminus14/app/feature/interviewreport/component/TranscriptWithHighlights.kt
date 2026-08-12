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
            var cursor = 0
            card.highlights
                .withIndex()
                .sortedBy { (_, span) -> span.startIndex }
                .forEach { (index, span) ->
                    if (span.startIndex >= cursor) {
                        if (span.startIndex > cursor) {
                            append(card.transcript.substring(cursor, span.startIndex))
                        }
                        val style = span.tone.toSpanStyle(colors)
                        pushStringAnnotation(HIGHLIGHT_ANNOTATION_TAG, index.toString())
                        withStyle(style) {
                            append(card.transcript.substring(span.startIndex, span.endIndex))
                        }
                        pop()
                        cursor = span.endIndex
                    }
                }
            if (cursor < card.transcript.length) {
                append(card.transcript.substring(cursor))
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
 * 다크 리포트 대본 하이라이트 색. 시안은 배경 없이 글자색만 강조한다.
 * - GOOD(잘함) → positive500 (cyan #00CFEF)
 * - IMPROVE(개선) → error400 (red #FF8383)
 * - 그 외(UNKNOWN 등) → 기본 대본색 유지
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
    return SpanStyle(
        background = Color.Transparent,
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

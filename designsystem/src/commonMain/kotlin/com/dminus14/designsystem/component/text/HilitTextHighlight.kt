package com.dminus14.designsystem.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

internal const val HILIT_TEXT_HIGHLIGHT_ANNOTATION_TAG = "hilit-text-highlight"

private val HIGHLIGHT_HORIZONTAL_PADDING = 8.dp
private val HIGHLIGHT_SLANT = 4.dp

enum class HilitTextHighlightColor {
    Green,
    Red,
    Blue,
}

/**
 * 블록에서 추가한 문자열을 Hilit 텍스트 하이라이트 범위로 표시한다.
 *
 * 표시한 범위의 타이포그래피는 [HilitText]의 부모 스타일을 따른다.
 */
fun AnnotatedString.Builder.withHilitTextHighlight(block: AnnotatedString.Builder.() -> Unit) {
    pushStringAnnotation(
        tag = HILIT_TEXT_HIGHLIGHT_ANNOTATION_TAG,
        annotation = HILIT_TEXT_HIGHLIGHT_ANNOTATION_TAG,
    )
    try {
        block()
    } finally {
        pop()
    }
}

/**
 * [withHilitTextHighlight] 범위를 [highlightColor] 색상의 Hilit 사다리꼴 하이라이트로 표시한다.
 *
 * 각 하이라이트 범위는 좌우 8dp 여백이 있고 내부에서 줄바꿈되지 않는 하나의 인라인 요소로
 * 배치된다.
 *
 * [onTextLayout]으로 전달되는 [TextLayoutResult]의 인덱스와 배치 정보는 원본 [text]가 아니라
 * 전처리된 `preparedText.text`를 기준으로 한다. 이 문자열에서는 각 하이라이트 범위가 하나의
 * 인라인 콘텐츠 플레이스홀더로 치환되므로 원본 [text]의 인덱스와 일치하지 않을 수 있다.
 *
 * Figma 노드 번호: 439-10362.
 */
@Composable
fun HilitText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    highlightColor: HilitTextHighlightColor = HilitTextHighlightColor.Green,
) {
    val highlightStyle =
        hilitTextHighlightStyle(
            color = highlightColor,
            colors = HilitTheme.colors,
        )
    val preparedText =
        prepareHilitText(
            text = text,
            style = style,
            highlightColor = highlightStyle.backgroundColor,
            highlightTextColor = highlightStyle.contentColor,
        )

    Text(
        text = preparedText.text,
        modifier =
            modifier.clearAndSetSemantics {
                this.text = text
            },
        color = color,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
        inlineContent = preparedText.inlineContent,
    )
}

@Composable
private fun prepareHilitText(
    text: AnnotatedString,
    style: TextStyle,
    highlightColor: Color,
    highlightTextColor: Color,
): PreparedHilitText {
    val textMeasurer = rememberTextMeasurer()
    val density = androidx.compose.ui.platform.LocalDensity.current

    return remember(text, style, highlightColor, highlightTextColor, textMeasurer, density) {
        val ranges = normalizedHighlightRanges(text)
        if (ranges.isEmpty()) {
            return@remember PreparedHilitText(text = text, inlineContent = emptyMap())
        }

        val renderedText = AnnotatedString.Builder()
        val inlineContent = mutableMapOf<String, InlineTextContent>()
        var sourceCursor = 0

        ranges.forEachIndexed { index, range ->
            renderedText.append(text.subSequence(sourceCursor, range.start))

            val highlightText =
                AnnotatedString
                    .Builder(text.subSequence(range.start, range.end))
                    .apply {
                        addStyle(
                            style = SpanStyle(color = highlightTextColor),
                            start = 0,
                            end = length,
                        )
                    }.toAnnotatedString()
            val measuredText =
                textMeasurer.measure(
                    text = highlightText,
                    style = style,
                    softWrap = false,
                    maxLines = 1,
                )
            val inlineContentId = "hilit-text-highlight-$index"
            val horizontalPaddingPx = with(density) { HIGHLIGHT_HORIZONTAL_PADDING.toPx() }
            val placeholderWidthPx =
                highlightPlaceholderWidth(
                    textWidthPx = measuredText.size.width.toFloat(),
                    horizontalPaddingPx = horizontalPaddingPx,
                )
            val placeholderWidth = with(density) { placeholderWidthPx.toDp().toSp() }
            val placeholderHeight =
                with(density) {
                    measuredText.size.height
                        .toDp()
                        .toSp()
                }

            renderedText.appendInlineContent(
                id = inlineContentId,
                alternateText = highlightText.text,
            )
            inlineContent[inlineContentId] =
                InlineTextContent(
                    placeholder =
                        Placeholder(
                            width = placeholderWidth,
                            height = placeholderHeight,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                        ),
                ) {
                    HilitHighlightInlineContent(
                        text = highlightText,
                        style = style,
                        backgroundColor = highlightColor,
                        density = density,
                    )
                }
            sourceCursor = range.end
        }

        renderedText.append(text.subSequence(sourceCursor, text.length))
        PreparedHilitText(
            text = renderedText.toAnnotatedString(),
            inlineContent = inlineContent,
        )
    }
}

@Composable
private fun HilitHighlightInlineContent(
    text: AnnotatedString,
    style: TextStyle,
    backgroundColor: Color,
    density: Density,
) {
    val slantPx = with(density) { HIGHLIGHT_SLANT.toPx() }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .drawBehind {
                    val vertices =
                        hilitTrapezoidVertices(
                            width = size.width,
                            height = size.height,
                            slant = slantPx,
                        )
                    val path =
                        Path().apply {
                            moveTo(vertices[0].x, vertices[0].y)
                            vertices.drop(1).forEach { vertex ->
                                lineTo(vertex.x, vertex.y)
                            }
                            close()
                        }
                    drawPath(path = path, color = backgroundColor)
                }.padding(horizontal = HIGHLIGHT_HORIZONTAL_PADDING),
    ) {
        Text(
            text = text,
            modifier = Modifier.clearAndSetSemantics {},
            style = style,
            softWrap = false,
            maxLines = 1,
        )
    }
}

internal data class HilitTextRange(
    val start: Int,
    val end: Int,
)

internal data class HilitTextHighlightStyle(
    val backgroundColor: Color,
    val contentColor: Color,
)

internal fun hilitTextHighlightStyle(
    color: HilitTextHighlightColor,
    colors: HilitColors,
): HilitTextHighlightStyle =
    when (color) {
        HilitTextHighlightColor.Green -> {
            HilitTextHighlightStyle(
                backgroundColor = colors.hilitGreen500,
                contentColor = colors.hilitBlack800,
            )
        }

        HilitTextHighlightColor.Red -> {
            HilitTextHighlightStyle(
                backgroundColor = colors.error200,
                contentColor = colors.error500,
            )
        }

        HilitTextHighlightColor.Blue -> {
            HilitTextHighlightStyle(
                backgroundColor = colors.positive200,
                contentColor = colors.positive800,
            )
        }
    }

internal fun normalizedHighlightRanges(text: AnnotatedString): List<HilitTextRange> {
    val ranges =
        text
            .getStringAnnotations(
                tag = HILIT_TEXT_HIGHLIGHT_ANNOTATION_TAG,
                start = 0,
                end = text.length,
            ).mapNotNull { range ->
                val start = range.start.coerceIn(0, text.length)
                val end = range.end.coerceIn(start, text.length)
                if (start == end) null else HilitTextRange(start = start, end = end)
            }.sortedBy(HilitTextRange::start)

    return ranges.fold(emptyList()) { merged, range ->
        val previous = merged.lastOrNull()
        if (previous == null || range.start > previous.end) {
            merged + range
        } else {
            merged.dropLast(1) + previous.copy(end = maxOf(previous.end, range.end))
        }
    }
}

internal fun highlightPlaceholderWidth(
    textWidthPx: Float,
    horizontalPaddingPx: Float,
): Float = textWidthPx + horizontalPaddingPx * 2

internal fun hilitTrapezoidVertices(
    width: Float,
    height: Float,
    slant: Float,
): List<Offset> =
    listOf(
        Offset(x = slant, y = 0f),
        Offset(x = width, y = 0f),
        Offset(x = width - slant, y = height),
        Offset(x = 0f, y = height),
    )

private data class PreparedHilitText(
    val text: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>,
)

@Preview(name = "HilitTextHighlight")
@Composable
private fun HilitTextHighlightPreview() {
    HilitTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HilitTextHighlightColor.entries.forEach { highlightColor ->
                HilitText(
                    text =
                        buildAnnotatedString {
                            append("면접에서 ")
                            withHilitTextHighlight {
                                append("핵심 경험")
                            }
                            append("을 설명해 주세요.")
                        },
                    style = HilitTheme.typography.body4,
                    highlightColor = highlightColor,
                )
            }
        }
    }
}

package com.dminus14.app.feature.interview.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme
import kotlin.math.roundToInt

private const val READINESS_TEXT = "곧 면접이 시작됩니다"
private const val MARQUEE_DURATION_MILLIS = 6_000
private const val BASE_MARQUEE_ITEM_COUNT = 3
private const val MARQUEE_EDGE_ITEM_COUNT = 2
private val TEXT_GAP = 6.dp

/**
 * 면접 시작 전 대기 상태를 나타내는 연속 텍스트 인디케이터.
 * 부모 폭과 텍스트 폭에 맞춰 문자열을 충분히 이어 붙이고, 우측에서 좌측으로 빈 구간 없이
 * 무한히 흐르게 한다. 중앙 텍스트만 상태에 따라 색상이 애니메이션으로 전환되고,
 * 좌측에서 사라진 뒤 다시 우측에서 나타난다.
 *
 * Figma Node: 683:9219 (준비 중), 683:9228 (준비 완료)
 */
@Composable
fun InterviewReadinessIndicator(
    isReady: Boolean,
    modifier: Modifier = Modifier,
    text: String = READINESS_TEXT,
) {
    val centerColor = rememberCenterColor(isReady)
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val textWidth =
        textMeasurer
            .measure(
                text = text,
                style = HilitTheme.typography.sub7,
                maxLines = 1,
                softWrap = false,
            ).size.width
    val textStride = textWidth + with(density) { TEXT_GAP.roundToPx() }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().background(HilitTheme.colors.gray800).clipToBounds(),
    ) {
        val itemCount = calculateMarqueeItemCount(constraints.maxWidth, textStride)
        val centerIndex = itemCount / 2
        val marqueeProgress = rememberMarqueeProgress(itemCount)

        InterviewReadinessMarquee(
            text = text,
            itemCount = itemCount,
            centerIndex = centerIndex,
            centerColor = centerColor,
            marqueeProgress = marqueeProgress,
        )
    }
}

@Composable
private fun InterviewReadinessMarquee(
    text: String,
    itemCount: Int,
    centerIndex: Int,
    centerColor: Color,
    marqueeProgress: Float,
) {
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            repeat(itemCount) { index ->
                Text(
                    text = text,
                    color =
                        if (index == centerIndex) {
                            centerColor
                        } else {
                            HilitTheme.colors.gray700
                        },
                    style = HilitTheme.typography.sub7,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                )
            }
        },
    ) { measurables, constraints ->
        val unboundedConstraints =
            Constraints(maxWidth = Constraints.Infinity, maxHeight = constraints.maxHeight)
        val placeables = measurables.map { it.measure(unboundedConstraints) }

        val rowWidth = constraints.constrainWidth(constraints.maxWidth)
        val measuredTextWidth = placeables.first().width
        val measuredTextStride = measuredTextWidth + TEXT_GAP.roundToPx()
        val cycleWidth = measuredTextStride * placeables.size
        val height = placeables.maxOf { it.height }

        layout(rowWidth, height) {
            val centerX = (rowWidth - measuredTextWidth) / 2f
            val wrapStart = -measuredTextWidth.toFloat()
            val offset = cycleWidth * marqueeProgress

            placeables.forEachIndexed { index, placeable ->
                val initialX = centerX + (index - centerIndex) * measuredTextStride
                val distanceFromWrapStart = initialX - offset - wrapStart
                val wrappedDistance =
                    ((distanceFromWrapStart % cycleWidth) + cycleWidth) % cycleWidth
                val x = wrapStart + wrappedDistance

                placeable.placeRelative(
                    x.roundToInt(),
                    (height - placeable.height) / 2,
                )
            }
        }
    }
}

@Composable
private fun rememberMarqueeProgress(itemCount: Int): Float {
    val infiniteTransition =
        rememberInfiniteTransition(label = "InterviewReadinessIndicatorMarquee")
    val durationMillis = MARQUEE_DURATION_MILLIS * itemCount / BASE_MARQUEE_ITEM_COUNT
    val progress by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = durationMillis,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "InterviewReadinessIndicatorMarqueeProgress",
        )
    return progress
}

internal fun calculateMarqueeItemCount(
    rowWidth: Int,
    textStride: Int,
): Int {
    require(rowWidth >= 0)
    require(textStride > 0)

    val visibleItemCount =
        ((rowWidth.toLong() + textStride - 1L) / textStride).toInt()
    return maxOf(BASE_MARQUEE_ITEM_COUNT, visibleItemCount + MARQUEE_EDGE_ITEM_COUNT)
}

@Composable
private fun rememberCenterColor(isReady: Boolean): Color {
    val color by
        animateColorAsState(
            targetValue =
                when (isReady) {
                    false -> HilitTheme.colors.gray600
                    true -> HilitTheme.colors.gray50
                },
            label = "InterviewReadinessIndicatorCenterColor",
        )
    return color
}

@Preview
@Composable
private fun InterviewReadinessIndicatorPreparingPreview() {
    HilitTheme {
        InterviewReadinessIndicator(isReady = false)
    }
}

@Preview
@Composable
private fun InterviewReadinessIndicatorReadyPreview() {
    HilitTheme {
        InterviewReadinessIndicator(isReady = true)
    }
}

@Preview
@Composable
private fun InterviewReadinessIndicatorNarrowPreview() {
    HilitTheme {
        InterviewReadinessIndicator(
            isReady = true,
            modifier = Modifier.width(80.dp),
        )
    }
}

package com.dminus14.app.feature.interview.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

private const val READINESS_TEXT = "곧 면접이 시작됩니다"
private val TEXT_GAP = 6.dp

/**
 * 면접 시작 전 대기 상태를 나타내는 3연속 텍스트 인디케이터.
 * 좌/중/우 텍스트를 이어붙인 전체 블록의 정중앙이 항상 부모의 정중앙에 오도록 배치하고,
 * 부모 폭이 부족하면 넘치는 부분은 잘려 보이지 않는다. 중앙 텍스트만 상태에 따라
 * 색상이 애니메이션으로 전환된다.
 *
 * Figma Node: 683:9219 (준비 중), 683:9228 (준비 완료)
 */
@Composable
fun InterviewReadinessIndicator(
    isReady: Boolean,
    modifier: Modifier = Modifier,
    text: String = READINESS_TEXT,
) {
    val centerColor by
        animateColorAsState(
            targetValue =
                when (isReady) {
                    false -> HilitTheme.colors.gray600
                    true -> HilitTheme.colors.gray50
                },
            label = "InterviewReadinessIndicatorCenterColor",
        )

    Layout(
        modifier = modifier.fillMaxWidth().background(HilitTheme.colors.gray800).clipToBounds(),
        content = {
            Text(
                text = text,
                color = HilitTheme.colors.gray700,
                style = HilitTheme.typography.sub7,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
            Text(
                text = text,
                color = centerColor,
                style = HilitTheme.typography.sub7,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
            Text(
                text = text,
                color = HilitTheme.colors.gray700,
                style = HilitTheme.typography.sub7,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
        },
    ) { measurables, constraints ->
        val gapPx = TEXT_GAP.roundToPx()
        val unboundedConstraints =
            Constraints(maxWidth = Constraints.Infinity, maxHeight = constraints.maxHeight)
        val placeables = measurables.map { it.measure(unboundedConstraints) }

        val rowWidth = constraints.constrainWidth(constraints.maxWidth)
        val contentWidth = placeables.sumOf { it.width } + gapPx * (placeables.size - 1)
        val height = placeables.maxOf { it.height }

        layout(rowWidth, height) {
            var x = (rowWidth - contentWidth) / 2
            placeables.forEach { placeable ->
                placeable.placeRelative(x, (height - placeable.height) / 2)
                x += placeable.width + gapPx
            }
        }
    }
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

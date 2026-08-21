package com.dminus14.designsystem.component.emptystate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

private val HilitEmptyStateMinHeight = 64.dp
private val HilitEmptyStateHorizontalPadding = 14.dp
private val HilitEmptyStateVerticalPadding = 14.dp
private val HilitEmptyStateBorderWidth = 1.dp
private val HilitEmptyStateDashOn = 6.dp
private val HilitEmptyStateDashOff = 4.dp

/**
 * 비어 있는 콘텐츠 영역을 안내 문구와 점선 테두리로 표시한다.
 *
 * Figma Node: 443:9714
 *
 * @param text 비어 있는 상태를 설명하는 안내 문구
 * @param modifier 외부 레이아웃 Modifier
 */
@Composable
fun HilitEmptyState(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = HilitEmptyStateMinHeight)
                .background(
                    color = HilitTheme.colors.hilitWhite,
                    shape = RectangleShape,
                ).dashedBorder(
                    width = HilitEmptyStateBorderWidth,
                    color = HilitTheme.colors.gray200,
                    dashOn = HilitEmptyStateDashOn,
                    dashOff = HilitEmptyStateDashOff,
                ).padding(
                    horizontal = HilitEmptyStateHorizontalPadding,
                    vertical = HilitEmptyStateVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HilitTheme.typography.body6,
            color = HilitTheme.colors.gray300,
        )
    }
}

private fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    dashOn: Dp,
    dashOff: Dp,
): Modifier =
    drawBehind {
        val strokeWidth = width.toPx()
        val dashPathEffect =
            PathEffect.dashPathEffect(
                floatArrayOf(dashOn.toPx(), dashOff.toPx()),
                0f,
            )
        drawRect(
            color = color,
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size =
                Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth,
                ),
            style =
                Stroke(
                    width = strokeWidth,
                    pathEffect = dashPathEffect,
                ),
        )
    }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun HilitEmptyStatePreview() {
    HilitTheme {
        HilitEmptyState(
            text = "아직 첨부된 콘텐츠가 없어요",
            modifier = Modifier.padding(16.dp),
        )
    }
}

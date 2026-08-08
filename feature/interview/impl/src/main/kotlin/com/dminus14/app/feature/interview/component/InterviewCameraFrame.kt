package com.dminus14.app.feature.interview.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

private val CORNER_DIRECTIONS =
    listOf(
        Offset(0f, 0f) to Offset(1f, 1f),
        Offset(1f, 0f) to Offset(-1f, 1f),
        Offset(0f, 1f) to Offset(1f, -1f),
        Offset(1f, 1f) to Offset(-1f, -1f),
    )

private fun DrawScope.drawCornerBrackets(
    color: Color,
    strokePx: Float,
    cornerPx: Float,
) {
    CORNER_DIRECTIONS.forEach { (originRatio, direction) ->
        val origin = Offset(originRatio.x * size.width, originRatio.y * size.height)
        drawLine(
            color = color,
            start = origin,
            end = origin + Offset(direction.x * cornerPx, 0f),
            strokeWidth = strokePx,
            cap = StrokeCap.Square,
        )
        drawLine(
            color = color,
            start = origin,
            end = origin + Offset(0f, direction.y * cornerPx),
            strokeWidth = strokePx,
            cap = StrokeCap.Square,
        )
    }
}

/**
 * Figma `camera-frame` preset (Figma Node: `683:9220`, `435:821`).
 *
 * 카메라 뷰 중앙에 위치하는 코너 브래킷 가이드 라인 및 가이드 텍스트.
 */
@Composable
fun InterviewCameraFrame(
    modifier: Modifier = Modifier,
    text: String = "얼굴을 여기에 맞춰주세요",
    color: Color = HilitTheme.colors.gray300.copy(alpha = 0xAC / 255f),
) {
    Box(
        modifier = modifier.size(327.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCornerBrackets(
                color = color,
                strokePx = 5.dp.toPx(),
                cornerPx = 35.dp.toPx(),
            )
        }

        if (text.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    color = HilitTheme.colors.gray400.copy(alpha = 0x9C / 255f),
                    style = HilitTheme.typography.head4,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CameraFramePreview() {
    HilitTheme {
        InterviewCameraFrame()
    }
}

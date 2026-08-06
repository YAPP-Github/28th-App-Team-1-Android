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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/**
 * Figma `camera-frame` preset (Figma Node: `683:9220`, `435:821`).
 *
 * 카메라 뷰 중앙에 위치하는 코너 브래킷 가이드 라인 및 가이드 텍스트.
 */
@Composable
fun InterviewCameraFrame(
    modifier: Modifier = Modifier,
    text: String = "얼굴을 여기에 맞춰주세요",
    showText: Boolean = true,
    frameSize: Dp = 327.dp,
    cornerLength: Dp = 35.dp,
    strokeWidth: Dp = 5.dp,
    color: Color = HilitTheme.colors.gray300.copy(alpha = 0xAC / 255f),
) {
    Box(
        modifier = modifier.size(frameSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val cornerPx = cornerLength.toPx()
            val width = size.width
            val height = size.height

            // Top-Left corner
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(cornerPx, 0f),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(0f, cornerPx),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )

            // Top-Right corner
            drawLine(
                color = color,
                start = Offset(width, 0f),
                end = Offset(width - cornerPx, 0f),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )
            drawLine(
                color = color,
                start = Offset(width, 0f),
                end = Offset(width, cornerPx),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )

            // Bottom-Left corner
            drawLine(
                color = color,
                start = Offset(0f, height),
                end = Offset(cornerPx, height),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )
            drawLine(
                color = color,
                start = Offset(0f, height),
                end = Offset(0f, height - cornerPx),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )

            // Bottom-Right corner
            drawLine(
                color = color,
                start = Offset(width, height),
                end = Offset(width - cornerPx, height),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )
            drawLine(
                color = color,
                start = Offset(width, height),
                end = Offset(width, height - cornerPx),
                strokeWidth = strokePx,
                cap = StrokeCap.Square,
            )
        }

        if (showText && text.isNotEmpty()) {
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

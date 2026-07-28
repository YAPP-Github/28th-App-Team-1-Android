package com.dminus14.designsystem.component.fileupload

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Android에서 CMP SVG가 렌더링되지 않는 동안의 임시 Canvas 아이콘.
 * SVG → XML Vector 전환 후 [HilitIcon]으로 교체한다.
 */
@Composable
internal fun FileUploadTempUploadIcon(
    backgroundColor: Color,
    foregroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = backgroundColor,
            cornerRadius = CornerRadius(w / 2f, h / 2f),
        )

        val stroke = w * (2f / 44f)
        // 위쪽 화살표 (upload.svg path)
        val arrow =
            Path().apply {
                moveTo(w * (29f / 44f), h * (19f / 44f))
                lineTo(w * (26f / 44f), h * (19f / 44f))
                lineTo(w * (22f / 44f), h * (14.5f / 44f))
                lineTo(w * (18f / 44f), h * (19f / 44f))
                lineTo(w * (15f / 44f), h * (19f / 44f))
                lineTo(w * (22f / 44f), h * (11f / 44f))
                close()
            }
        drawPath(path = arrow, color = foregroundColor)

        drawLine(
            color = foregroundColor,
            start = Offset(x = w * (22f / 44f), y = h * (13.3f / 44f)),
            end = Offset(x = w * (22f / 44f), y = h * (25f / 44f)),
            strokeWidth = stroke,
            cap = StrokeCap.Butt,
        )
        drawLine(
            color = foregroundColor,
            start = Offset(x = w * (15f / 44f), y = h * (30f / 44f)),
            end = Offset(x = w * (29f / 44f), y = h * (30f / 44f)),
            strokeWidth = stroke,
            cap = StrokeCap.Butt,
        )
    }
}

@Composable
internal fun FileUploadTempFileIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        // viewBox 20x24, stroke-width 4 → scale to canvas
        val sx = size.width / 20f
        val sy = size.height / 24f
        val stroke = 2.5f * minOf(sx, sy)
        val path =
            Path().apply {
                moveTo(18f * sx, 6.54545f * sy)
                lineTo(13.5561f * sx, 2f * sy)
                lineTo(2f * sx, 2f * sy)
                lineTo(2f * sx, 22f * sy)
                lineTo(18f * sx, 22f * sy)
                lineTo(18f * sx, 6.54545f * sy)
                moveTo(13.5561f * sx, 2f * sy)
                lineTo(13.5561f * sx, 6.54545f * sy)
                lineTo(18f * sx, 6.54545f * sy)
            }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke),
        )
    }
}

@Composable
internal fun FileUploadTempCloseIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.12f
        val inset = size.minDimension * 0.2f
        drawLine(
            color = color,
            start = Offset(inset, inset),
            end = Offset(size.width - inset, size.height - inset),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width - inset, inset),
            end = Offset(inset, size.height - inset),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

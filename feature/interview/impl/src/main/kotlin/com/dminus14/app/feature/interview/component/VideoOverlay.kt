package com.dminus14.app.feature.interview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

private const val GRADIENT_STOP = 0.91542f

enum class VideoOverlayDirection {
    TOP,
    BOTTOM,
}

/**
 * 카메라 프리뷰 위에 얹는 검정 그라데이션 오버레이.
 *
 * Figma Node: 683:9224
 */
@Composable
fun VideoOverlay(
    modifier: Modifier = Modifier,
    direction: VideoOverlayDirection = VideoOverlayDirection.BOTTOM,
) {
    val black = HilitTheme.colors.hilitBlack900
    val brush =
        when (direction) {
            VideoOverlayDirection.BOTTOM -> {
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    GRADIENT_STOP to black,
                )
            }

            VideoOverlayDirection.TOP -> {
                Brush.verticalGradient(
                    (1f - GRADIENT_STOP) to black,
                    1f to Color.Transparent,
                )
            }
        }

    Box(
        modifier = modifier.background(brush = brush),
    )
}

@Preview
@Composable
private fun VideoOverlayBottomPreview() {
    HilitTheme {
        VideoOverlay(
            modifier = Modifier.fillMaxSize().height(229.dp),
            direction = VideoOverlayDirection.BOTTOM,
        )
    }
}

@Preview
@Composable
private fun VideoOverlayTopPreview() {
    HilitTheme {
        VideoOverlay(
            modifier = Modifier.fillMaxSize().height(229.dp),
            direction = VideoOverlayDirection.TOP,
        )
    }
}

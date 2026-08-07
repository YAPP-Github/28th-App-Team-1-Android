package com.dminus14.designsystem.component.loading

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.loading_indicator
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.sin

/**
 * 공용 로딩 인디케이터.
 *
 * Figma 노드 번호: 439-10407.
 */
@Composable
fun HilitLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = DEFAULT_LOADING_INDICATOR_SIZE,
    contentDescription: String? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HilitLoadingIndicator")
    val rotation by
        infiniteTransition.animateFloat(
            initialValue = LOADING_INDICATOR_START_DEGREES,
            targetValue = LOADING_INDICATOR_END_DEGREES,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = LOADING_INDICATOR_DURATION_MILLIS,
                            easing = LOADING_INDICATOR_EASING,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "HilitLoadingIndicatorRotation",
        )
    val geometry = hilitLoadingIndicatorGeometry(size)
    val accessibilityModifier =
        if (contentDescription == null) {
            Modifier
        } else {
            Modifier.semantics { this.contentDescription = contentDescription }
        }

    Box(
        modifier =
            modifier
                .size(geometry.containerSize)
                .progressSemantics()
                .then(accessibilityModifier),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.loading_indicator),
            contentDescription = null,
            modifier =
                Modifier
                    .size(
                        width = geometry.graphicWidth,
                        height = geometry.graphicHeight,
                    ).rotate(rotation),
        )
    }
}

internal data class HilitLoadingIndicatorGeometry(
    val containerSize: Dp,
    val graphicWidth: Dp,
    val graphicHeight: Dp,
)

internal fun hilitLoadingIndicatorGeometry(size: Dp): HilitLoadingIndicatorGeometry {
    require(size > 0.dp) { "HilitLoadingIndicator size must be positive." }
    return HilitLoadingIndicatorGeometry(
        containerSize = size,
        graphicWidth = size * LOADING_INDICATOR_WIDTH_RATIO,
        graphicHeight = size * LOADING_INDICATOR_HEIGHT_RATIO,
    )
}

internal const val LOADING_INDICATOR_DURATION_MILLIS = 1_500
internal const val LOADING_INDICATOR_START_DEGREES = 0f
internal const val LOADING_INDICATOR_END_DEGREES = 360f
internal val LOADING_INDICATOR_EASING =
    Easing { fraction ->
        val radians = LOADING_INDICATOR_FULL_CYCLE_RADIANS * fraction
        fraction -
            LOADING_INDICATOR_SPEED_VARIATION /
            LOADING_INDICATOR_FULL_CYCLE_RADIANS *
            sin(radians)
    }

private const val LOADING_INDICATOR_WIDTH_RATIO = 72f / 74f
private const val LOADING_INDICATOR_HEIGHT_RATIO = 73f / 74f
private const val LOADING_INDICATOR_SPEED_VARIATION = 0.65f
private const val LOADING_INDICATOR_FULL_CYCLE_RADIANS = (2.0 * PI).toFloat()
private val DEFAULT_LOADING_INDICATOR_SIZE = 74.dp

@Preview(name = "HilitLoadingIndicator")
@Composable
private fun HilitLoadingIndicatorPreview() {
    HilitTheme {
        Column {
            HilitLoadingIndicator(contentDescription = "불러오는 중")
            HilitLoadingIndicator(size = 48.dp)
        }
    }
}

package com.dminus14.designsystem.component.toggle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun HilitToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = hilitToggleStyle(checked = checked, colors = HilitTheme.colors)
    val interactionSource = remember { MutableInteractionSource() }
    val animatedThumbOffset by
        animateDpAsState(
            targetValue = style.thumbOffset,
            animationSpec = tween(durationMillis = TOGGLE_ANIMATION_DURATION_MILLIS),
            label = "HilitToggleThumbOffset",
        )
    val animatedThumbColor by
        animateColorAsState(
            targetValue = style.thumbColor,
            animationSpec = tween(durationMillis = TOGGLE_ANIMATION_DURATION_MILLIS),
            label = "HilitToggleThumbColor",
        )

    Box(
        modifier =
            modifier
                .requiredSize(width = ToggleWidth, height = ToggleHeight)
                .background(style.trackColor)
                .toggleable(
                    value = checked,
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Switch,
                    onValueChange = onCheckedChange,
                ).padding(TogglePadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .offset(x = animatedThumbOffset)
                    .requiredSize(ToggleThumbSize)
                    .background(animatedThumbColor),
        )
    }
}

internal data class HilitToggleStyle(
    val trackColor: Color,
    val thumbColor: Color,
    val thumbOffset: Dp,
)

internal fun hilitToggleStyle(
    checked: Boolean,
    colors: HilitColors,
): HilitToggleStyle =
    HilitToggleStyle(
        trackColor = colors.gray900,
        thumbColor = if (checked) colors.hilitGreen500 else colors.gray50,
        thumbOffset = if (checked) ToggleThumbTravelDistance else 0.dp,
    )

internal const val TOGGLE_ANIMATION_DURATION_MILLIS = 200

private val ToggleWidth = 50.dp
private val ToggleHeight = 28.dp
private val TogglePadding = 4.dp
private val ToggleThumbSize = 20.dp
private val ToggleThumbTravelDistance = 22.dp

@Preview(name = "HilitToggle")
@Composable
private fun HilitTogglePreview() {
    HilitTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement =
                androidx.compose.foundation.layout.Arrangement
                    .spacedBy(16.dp),
        ) {
            HilitToggle(checked = true, onCheckedChange = {})
            HilitToggle(checked = false, onCheckedChange = {})
        }
    }
}

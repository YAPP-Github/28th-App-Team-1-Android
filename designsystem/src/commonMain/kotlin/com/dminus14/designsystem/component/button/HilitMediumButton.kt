package com.dminus14.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

enum class HilitMediumButtonColor {
    Default,
    Gray,
    Blue,
    Red,
    Green,
    Black,
}

/**
 * 색상이 있는 중간 사이즈 버튼.
 *
 * Figma 노드 번호 439-10175, 439-10176, 439-10178, 439-10187, 439-10188, 439-10189.
 */
@Composable
fun HilitMediumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: HilitMediumButtonColor = HilitMediumButtonColor.Default,
    enabled: Boolean = true,
) {
    val colors = HilitTheme.colors
    val style = hilitMediumButtonStyle(color = color, enabled = enabled, colors = colors)
    val textStyle =
        when (style.typography) {
            HilitMediumButtonTypography.Body2 -> HilitTheme.typography.body2
            HilitMediumButtonTypography.Body3 -> HilitTheme.typography.body3
        }
    val interactionSource = remember { MutableInteractionSource() }
    val outlineModifier =
        style.outlineColor?.let { outlineColor ->
            Modifier.border(
                width = MediumButtonOutlineWidth,
                color = outlineColor,
                shape = RectangleShape,
            )
        } ?: Modifier

    Box(
        modifier =
            modifier
                .background(color = style.backgroundColor, shape = RectangleShape)
                .then(outlineModifier)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).padding(
                    horizontal = MediumButtonHorizontalPadding,
                    vertical = MediumButtonVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = style.contentColor,
            style = textStyle,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal enum class HilitMediumButtonTypography {
    Body2,
    Body3,
}

internal data class HilitMediumButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val outlineColor: Color?,
    val typography: HilitMediumButtonTypography,
)

internal fun hilitMediumButtonStyle(
    color: HilitMediumButtonColor,
    enabled: Boolean,
    colors: HilitColors,
): HilitMediumButtonStyle {
    if (!enabled) {
        return HilitMediumButtonStyle(
            backgroundColor = colors.gray50,
            contentColor = colors.gray300,
            outlineColor = colors.gray300,
            typography = HilitMediumButtonTypography.Body3,
        )
    }

    return when (color) {
        HilitMediumButtonColor.Default -> {
            HilitMediumButtonStyle(
                backgroundColor = colors.hilitWhite,
                contentColor = colors.hilitBlack800,
                outlineColor = colors.gray200,
                typography = HilitMediumButtonTypography.Body3,
            )
        }

        HilitMediumButtonColor.Gray -> {
            HilitMediumButtonStyle(
                backgroundColor = colors.hilitWhite,
                contentColor = colors.gray700,
                outlineColor = colors.gray100,
                typography = HilitMediumButtonTypography.Body3,
            )
        }

        HilitMediumButtonColor.Blue -> {
            HilitMediumButtonStyle(
                backgroundColor = colors.positive200,
                contentColor = colors.positive800,
                outlineColor = colors.positive500,
                typography = HilitMediumButtonTypography.Body2,
            )
        }

        HilitMediumButtonColor.Red -> {
            HilitMediumButtonStyle(
                backgroundColor = colors.error200,
                contentColor = colors.error500,
                outlineColor = colors.error500,
                typography = HilitMediumButtonTypography.Body2,
            )
        }

        HilitMediumButtonColor.Green -> {
            HilitMediumButtonStyle(
                backgroundColor = colors.hilitGreen500,
                contentColor = colors.hilitGreen800,
                outlineColor = colors.hilitGreen600,
                typography = HilitMediumButtonTypography.Body3,
            )
        }

        HilitMediumButtonColor.Black -> {
            HilitMediumButtonStyle(
                backgroundColor = colors.hilitBlack800,
                contentColor = colors.hilitWhite,
                outlineColor = null,
                typography = HilitMediumButtonTypography.Body3,
            )
        }
    }
}

private val MediumButtonHorizontalPadding = 0.dp
private val MediumButtonVerticalPadding = 12.dp
private val MediumButtonOutlineWidth = 1.2.dp

@Preview(name = "HilitMediumButton")
@Composable
private fun HilitMediumButtonPreview() {
    HilitTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HilitMediumButton(
                    text = "Default",
                    color = HilitMediumButtonColor.Default,
                    onClick = {},
                )
                HilitMediumButton(
                    text = "Green",
                    color = HilitMediumButtonColor.Green,
                    onClick = {},
                )
                HilitMediumButton(text = "Disabled", enabled = false, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HilitMediumButton(text = "Blue", color = HilitMediumButtonColor.Blue, onClick = {})
                HilitMediumButton(
                    text = "Black",
                    color = HilitMediumButtonColor.Black,
                    onClick = {},
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HilitMediumButton(text = "Red", color = HilitMediumButtonColor.Red, onClick = {})
                HilitMediumButton(text = "Gray", color = HilitMediumButtonColor.Gray, onClick = {})
            }
        }
    }
}

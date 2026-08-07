package com.dminus14.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

enum class HilitMiniButtonColor {
    Light,
    Dark,
    Non,
}

/**
 * 미니 버튼.
 *
 * Figma 노드 번호: 439-10190, 439-10191, 439-10192, 439-10193, 439-10194, 439-10195, 439-10196, 439-10197.
 */
@Composable
fun HilitMiniButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: HilitMiniButtonColor = HilitMiniButtonColor.Light,
    content: @Composable () -> Unit,
) {
    val style = hilitMiniButtonStyle(color = color, colors = HilitTheme.colors)
    val interactionSource = remember { MutableInteractionSource() }

    CompositionLocalProvider(LocalContentColor provides style.contentColor) {
        ProvideTextStyle(HilitTheme.typography.body5) {
            Row(
                modifier =
                    modifier
                        .background(style.backgroundColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        ).padding(style.contentPadding),
                horizontalArrangement = Arrangement.spacedBy(style.contentSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                content()
            }
        }
    }
}

internal data class HilitMiniButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val contentPadding: Dp,
    val contentSpacing: Dp,
)

internal fun hilitMiniButtonStyle(
    color: HilitMiniButtonColor,
    colors: HilitColors,
): HilitMiniButtonStyle =
    when (color) {
        HilitMiniButtonColor.Light -> {
            HilitMiniButtonStyle(
                backgroundColor = colors.gray100,
                contentColor = colors.hilitBlack800,
                contentPadding = 8.dp,
                contentSpacing = 8.dp,
            )
        }

        HilitMiniButtonColor.Dark -> {
            HilitMiniButtonStyle(
                backgroundColor = colors.gray900,
                contentColor = colors.hilitWhite,
                contentPadding = 8.dp,
                contentSpacing = 8.dp,
            )
        }

        HilitMiniButtonColor.Non -> {
            HilitMiniButtonStyle(
                backgroundColor = Color.Unspecified,
                contentColor = colors.gray400,
                contentPadding = 8.dp,
                contentSpacing = 8.dp,
            )
        }
    }

@Composable
internal fun HilitMiniButtonSampleContent(text: String) {
    HilitIcon(
        asset = HilitIconAsset.Video,
        contentDescription = null,
        tint = LocalContentColor.current,
        modifier = Modifier.size(16.dp),
    )
    Text(text = text)
}

@Preview(name = "HilitMiniButton")
@Composable
private fun HilitMiniButtonPreview() {
    HilitTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HilitMiniButton(onClick = {}) {
                HilitMiniButtonSampleContent(text = "버튼")
            }
            HilitMiniButton(
                color = HilitMiniButtonColor.Dark,
                onClick = {},
            ) {
                HilitMiniButtonSampleContent(text = "버튼")
            }
            HilitMiniButton(onClick = {}) {
                Text(text = "텍스트만 사용")
            }

            HilitMiniButton(
                color = HilitMiniButtonColor.Non,
                onClick = {},
            ) {
                Text(text = "텍스트만 사용")
            }
        }
    }
}

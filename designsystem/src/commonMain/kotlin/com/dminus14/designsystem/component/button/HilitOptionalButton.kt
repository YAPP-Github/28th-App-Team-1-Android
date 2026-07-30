package com.dminus14.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun HilitOptionalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val style = hilitOptionalButtonStyle(HilitTheme.colors)
    val interactionSource = remember { MutableInteractionSource() }

    CompositionLocalProvider(LocalContentColor provides style.contentColor) {
        ProvideTextStyle(HilitTheme.typography.body6) {
            Row(
                modifier =
                    modifier
                        .heightIn(min = style.minHeight)
                        .background(style.backgroundColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        ).optionalButtonBorder(style)
                        .padding(style.contentPadding),
                horizontalArrangement = Arrangement.spacedBy(style.contentSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                content()
            }
        }
    }
}

internal data class HilitOptionalButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val outlineColor: Color,
    val outlineWidth: Dp,
    val dashLength: Dp,
    val dashGap: Dp,
    val minHeight: Dp,
    val contentPadding: Dp,
    val contentSpacing: Dp,
)

internal fun hilitOptionalButtonStyle(colors: HilitColors): HilitOptionalButtonStyle =
    HilitOptionalButtonStyle(
        backgroundColor = colors.hilitWhite,
        contentColor = colors.gray900,
        outlineColor = colors.gray100,
        outlineWidth = 1.2.dp,
        dashLength = 4.dp,
        dashGap = 4.dp,
        minHeight = 42.dp,
        contentPadding = 12.dp,
        contentSpacing = 8.dp,
    )

private fun Modifier.optionalButtonBorder(style: HilitOptionalButtonStyle): Modifier =
    drawWithContent {
        drawContent()
        val strokeWidth = style.outlineWidth.toPx()
        val inset = strokeWidth / 2f
        drawRect(
            color = style.outlineColor,
            topLeft = Offset(inset, inset),
            size = Size(width = size.width - strokeWidth, height = size.height - strokeWidth),
            style =
                Stroke(
                    width = strokeWidth,
                    pathEffect =
                        PathEffect.dashPathEffect(
                            intervals =
                                floatArrayOf(
                                    style.dashLength.toPx(),
                                    style.dashGap.toPx(),
                                ),
                        ),
                ),
        )
    }

@Composable
internal fun HilitOptionalButtonSampleContent(
    text: String,
    tagText: String,
    showIcon: Boolean = true,
    showTag: Boolean = true,
) {
    if (showIcon) {
        Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            HilitIcon(
                asset = HilitIconAsset.Plus,
                contentDescription = null,
                tint = LocalContentColor.current,
                modifier = Modifier.size(12.dp),
            )
        }
    }
    Text(text = text)
    if (showTag) {
        HilitTag(
            colorType = TagColorType.Gray,
            tagType = TagType.Small,
            text = tagText,
        )
    }
}

@Preview(name = "HilitOptionalButton")
@Composable
private fun HilitOptionalButtonPreview() {
    HilitTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HilitOptionalButton(onClick = {}) {
                HilitOptionalButtonSampleContent(text = "버튼", tagText = "선택")
            }
            HilitOptionalButton(onClick = {}) {
                HilitOptionalButtonSampleContent(
                    text = "조금 더 긴 버튼 문구",
                    tagText = "선택",
                )
            }
            HilitOptionalButton(
                modifier = Modifier.width(334.dp),
                onClick = {},
            ) {
                HilitOptionalButtonSampleContent(text = "버튼", tagText = "선택")
            }
        }
    }
}

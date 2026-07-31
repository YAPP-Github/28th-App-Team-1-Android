package com.dminus14.designsystem.component.tab

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun HilitTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val style =
        hilitTabStyle(
            selected = selected,
            enabled = enabled,
            colors = HilitTheme.colors,
        )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .selectable(
                    selected = selected,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Tab,
                    onClick = onClick,
                ).drawBehind {
                    if (style.showIndicator) {
                        val strokeWidth = style.indicatorWidth.toPx()
                        drawLine(
                            color = style.indicatorColor,
                            start = Offset(x = 0f, y = size.height - strokeWidth / 2f),
                            end = Offset(x = size.width, y = size.height - strokeWidth / 2f),
                            strokeWidth = strokeWidth,
                        )
                    }
                }.padding(
                    horizontal = TabHorizontalPadding,
                    vertical = TabVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = style.contentColor,
            style = HilitTheme.typography.body2,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal data class HilitTabStyle(
    val contentColor: Color,
    val indicatorColor: Color,
    val indicatorWidth: Dp,
    val showIndicator: Boolean,
)

internal fun hilitTabStyle(
    selected: Boolean,
    enabled: Boolean,
    colors: HilitColors,
): HilitTabStyle =
    HilitTabStyle(
        contentColor = if (enabled) colors.hilitBlack800 else colors.gray500,
        indicatorColor = colors.hilitBlack800,
        indicatorWidth = 1.5.dp,
        showIndicator = selected && enabled,
    )

private val TabHorizontalPadding = 14.dp
private val TabVerticalPadding = 8.dp

@Preview(name = "HilitTab")
@Composable
private fun HilitTabPreview() {
    HilitTheme {
        Column {
            Row {
                HilitTab(text = "선택", selected = true, onClick = {})
                HilitTab(text = "기본", selected = false, onClick = {})
                HilitTab(
                    text = "비활성",
                    selected = false,
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}

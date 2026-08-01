package com.dminus14.designsystem.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 선택 가능한 Chip.
 *
 * @param text Chip에 표시할 텍스트
 * @param selected 선택 여부
 * @param onClick 클릭 콜백
 * @param modifier 외부 Modifier
 * @param enabled 클릭 가능 여부
 */
@Composable
fun HilitChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = HilitTheme.colors
    val style = hilitChipStyle(selected = selected, enabled = enabled, colors = colors)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .background(color = style.backgroundColor, shape = RectangleShape)
                .border(
                    width = ChipBorderWidth,
                    color = style.outlineColor,
                    shape = RectangleShape,
                ).clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ).padding(
                    horizontal = ChipHorizontalPadding,
                    vertical = ChipVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = style.contentColor,
            style = HilitTheme.typography.body3,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal data class HilitChipStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val outlineColor: Color,
)

internal fun hilitChipStyle(
    selected: Boolean,
    enabled: Boolean,
    colors: HilitColors,
): HilitChipStyle {
    if (!enabled) {
        return HilitChipStyle(
            backgroundColor = colors.gray50,
            contentColor = colors.gray300,
            outlineColor = colors.gray300,
        )
    }

    return if (selected) {
        HilitChipStyle(
            backgroundColor = colors.hilitGreen500,
            contentColor = colors.hilitGreen800,
            outlineColor = colors.hilitGreen600,
        )
    } else {
        HilitChipStyle(
            backgroundColor = colors.hilitWhite,
            contentColor = colors.hilitBlack800,
            outlineColor = colors.gray200,
        )
    }
}

private val ChipHorizontalPadding = 32.dp
private val ChipVerticalPadding = 16.dp
private val ChipBorderWidth = 1.dp

@Preview(name = "HilitChip", showBackground = true)
@Composable
private fun HilitChipPreview() {
    HilitTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HilitChip(text = "백엔드", selected = false, onClick = {})
            HilitChip(text = "iOS", selected = true, onClick = {})
            HilitChip(text = "Disabled", selected = false, enabled = false, onClick = {})
        }
    }
}

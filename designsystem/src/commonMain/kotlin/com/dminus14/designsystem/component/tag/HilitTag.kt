package com.dminus14.designsystem.component.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

enum class TagColorType {
    BlackGreen,
    BlackWhite,
    Gray,
    Green,
    Red,
    Blue,
}

enum class TagType {
    Large,
    Small,
}

@Composable
fun HilitTag(
    modifier: Modifier = Modifier,
    colorType: TagColorType,
    tagType: TagType,
    text: String,
) {
    val colors = HilitTheme.colors
    val color = tagColors(type = colorType, colors = colors)
    val horizontalPadding =
        when (tagType) {
            TagType.Small -> 4.dp
            TagType.Large -> 12.dp
        }
    val verticalPadding =
        when (tagType) {
            TagType.Small -> 0.dp
            TagType.Large -> 4.dp
        }
    val textStyle =
        if (tagType == TagType.Small) {
            HilitTheme.typography.body5
        } else {
            HilitTheme.typography.body6
        }

    Box(
        modifier =
            modifier
                .background(
                    color = color.backgroundColor,
                    shape = RectangleShape,
                ).padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = textStyle,
            color = color.contentColor,
        )
    }
}

@Composable
private fun tagColors(
    type: TagColorType,
    colors: HilitColors,
): TagColorSet =
    when (type) {
        TagColorType.BlackGreen -> {
            TagColorSet(
                backgroundColor = colors.hilitBlack800,
                contentColor = colors.hilitGreen500,
            )
        }

        TagColorType.BlackWhite -> {
            TagColorSet(
                backgroundColor = colors.hilitBlack800,
                contentColor = colors.hilitWhite,
            )
        }

        TagColorType.Gray -> {
            TagColorSet(
                backgroundColor = colors.gray100,
                contentColor = colors.gray600,
            )
        }

        TagColorType.Green -> {
            TagColorSet(
                backgroundColor = colors.hilitGreen500,
                contentColor = colors.hilitGreen800,
            )
        }

        TagColorType.Red -> {
            TagColorSet(
                backgroundColor = colors.error200,
                contentColor = colors.error500,
            )
        }

        TagColorType.Blue -> {
            TagColorSet(
                backgroundColor = colors.positive200,
                contentColor = colors.positive800,
            )
        }
    }

@Preview(name = "HilitTagSmall")
@Composable
private fun HilitTagPreview() {
    HilitTheme {
        Column {
            Row(
                modifier =
                    Modifier
                        .background(HilitTheme.colors.hilitWhite)
                        .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitTag(
                    colorType = TagColorType.BlackGreen,
                    tagType = TagType.Small,
                    text = "Black",
                )
                HilitTag(colorType = TagColorType.Gray, tagType = TagType.Small, text = "Gray")
                HilitTag(colorType = TagColorType.Green, tagType = TagType.Small, text = "Green")
                HilitTag(colorType = TagColorType.Red, tagType = TagType.Small, text = "Red")
                HilitTag(colorType = TagColorType.Blue, tagType = TagType.Small, text = "Blue")
            }

            Row(
                modifier =
                    Modifier
                        .background(HilitTheme.colors.hilitWhite)
                        .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitTag(
                    colorType = TagColorType.BlackGreen,
                    tagType = TagType.Large,
                    text = "Black",
                )
                HilitTag(colorType = TagColorType.Gray, tagType = TagType.Large, text = "Gray")
                HilitTag(colorType = TagColorType.Green, tagType = TagType.Large, text = "Green")
                HilitTag(colorType = TagColorType.Red, tagType = TagType.Large, text = "Red")
                HilitTag(colorType = TagColorType.Blue, tagType = TagType.Large, text = "Blue")
            }
        }
    }
}

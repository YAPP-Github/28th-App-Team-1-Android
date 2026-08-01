package com.dminus14.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 하단 고정 듀얼 버튼의 시각 타입.
 *
 * - [Default]: 양쪽 모두 검정 배경·흰 글자
 * - [Gray]: 양쪽 모두 흰 배경·회색 글자
 * - [TwoColor]: 왼쪽은 Gray, 오른쪽은 Default
 */
enum class HilitFixedBottomDualButtonType {
    Default,
    Gray,
    TwoColor,
}

@Composable
fun HilitFixedBottomDualButton(
    modifier: Modifier = Modifier,
    leftText: String = "",
    rightText: String = "",
    leftEnabled: Boolean = true,
    rightEnabled: Boolean = true,
    type: HilitFixedBottomDualButtonType = HilitFixedBottomDualButtonType.Default,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
) {
    val colors = HilitTheme.colors
    val leftColor =
        dualButtonLeftSide(
            type = type,
            colors = colors,
        )
    val rightColor =
        dualButtonRightSide(
            type = type,
            colors = colors,
        )
    val background = backgroundColor(colors = colors, type = type)
    val dividerColor = hilitFixedBottomDualButtonDividerColor(type = type, colors = colors)

    Row(
        modifier =
            modifier
                .background(background)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DualButtonSide(
            text = leftText,
            enabled = leftEnabled,
            color = leftColor,
            onClick = onLeftClick,
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.weight(1f),
        )

        if (type != HilitFixedBottomDualButtonType.TwoColor) {
            Box(
                modifier =
                    Modifier
                        .width(1.dp)
                        .height(25.dp)
                        .background(dividerColor),
            )
        }

        DualButtonSide(
            text = rightText,
            enabled = rightEnabled,
            color = rightColor,
            onClick = onRightClick,
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DualButtonSide(
    text: String,
    enabled: Boolean,
    color: BtnColorSet,
    onClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val contentColor =
        when {
            enabled -> color.contentColor
            !enabled -> HilitTheme.colors.gray400
            else -> color.backgroundColor
        }

    Box(
        modifier =
            modifier
                .background(color.backgroundColor)
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                ).padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HilitTheme.typography.sub7,
            color = contentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun backgroundColor(
    colors: HilitColors,
    type: HilitFixedBottomDualButtonType,
): Color =
    when (type) {
        HilitFixedBottomDualButtonType.Default -> colors.hilitBlack800
        HilitFixedBottomDualButtonType.Gray -> Color.Unspecified
        HilitFixedBottomDualButtonType.TwoColor -> Color.Unspecified
    }

private fun dualButtonLeftSide(
    colors: HilitColors,
    type: HilitFixedBottomDualButtonType,
): BtnColorSet =
    when (type) {
        HilitFixedBottomDualButtonType.Default -> {
            BtnColorSet(
                contentColor = colors.hilitWhite,
                backgroundColor = colors.hilitBlack800,
                pressColor = colors.hilitWhite, // BtnColorSet에 맞출뿐 실제로 사용되지 않습니다.
            )
        }

        HilitFixedBottomDualButtonType.Gray -> {
            BtnColorSet(
                contentColor = colors.hilitBlack800,
                backgroundColor = Color.Unspecified,
                pressColor = colors.hilitWhite, // BtnColorSet에 맞출뿐 실제로 사용되지 않습니다.
            )
        }

        HilitFixedBottomDualButtonType.TwoColor -> {
            BtnColorSet(
                contentColor = colors.gray700,
                backgroundColor = Color.Unspecified,
                pressColor = colors.hilitWhite, // BtnColorSet에 맞출뿐 실제로 사용되지 않습니다.
            )
        }
    }

private fun dualButtonRightSide(
    colors: HilitColors,
    type: HilitFixedBottomDualButtonType,
): BtnColorSet =
    when (type) {
        HilitFixedBottomDualButtonType.Default -> {
            BtnColorSet(
                contentColor = colors.hilitWhite,
                backgroundColor = colors.hilitBlack800,
                pressColor = colors.hilitWhite, // BtnColorSet에 맞출뿐 실제로 사용되지 않습니다.
            )
        }

        HilitFixedBottomDualButtonType.Gray -> {
            BtnColorSet(
                contentColor = colors.hilitBlack800,
                backgroundColor = Color.Unspecified,
                pressColor = colors.hilitWhite, // BtnColorSet에 맞출뿐 실제로 사용되지 않습니다.
            )
        }

        HilitFixedBottomDualButtonType.TwoColor -> {
            BtnColorSet(
                contentColor = colors.hilitWhite,
                backgroundColor = colors.hilitBlack800,
                pressColor = colors.hilitWhite, // BtnColorSet에 맞출뿐 실제로 사용되지 않습니다.
            )
        }
    }

private fun hilitFixedBottomDualButtonDividerColor(
    type: HilitFixedBottomDualButtonType,
    colors: HilitColors,
): Color =
    when (type) {
        HilitFixedBottomDualButtonType.Default -> colors.gray800
        HilitFixedBottomDualButtonType.Gray -> colors.gray200
        HilitFixedBottomDualButtonType.TwoColor -> colors.gray800
    }

@Preview(name = "HilitFixedBottomDualButton")
@Composable
private fun HilitFixedBottomDualButtonPreview() {
    HilitTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(HilitTheme.colors.hilitWhite),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HilitFixedBottomDualButton(
                    leftText = "버튼",
                    rightText = "버튼2",
                    type = HilitFixedBottomDualButtonType.Default,
                    onLeftClick = {},
                    onRightClick = {},
                )
                HilitFixedBottomDualButton(
                    leftText = "버튼",
                    rightText = "버튼2",
                    type = HilitFixedBottomDualButtonType.Gray,
                    onLeftClick = {},
                    onRightClick = {},
                )
                HilitFixedBottomDualButton(
                    leftText = "버튼",
                    rightText = "버튼2",
                    type = HilitFixedBottomDualButtonType.TwoColor,
                    onLeftClick = {},
                    onRightClick = {},
                )
                HilitFixedBottomDualButton(
                    leftText = "버튼",
                    rightText = "버튼2",
                    leftEnabled = false,
                    rightEnabled = true,
                    type = HilitFixedBottomDualButtonType.Default,
                    onLeftClick = {},
                    onRightClick = {},
                )
            }
        }
    }
}

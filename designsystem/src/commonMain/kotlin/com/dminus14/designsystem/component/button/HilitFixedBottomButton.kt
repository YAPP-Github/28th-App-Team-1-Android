package com.dminus14.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 버튼이 표시되는 화면 모드에 따른 색상 타입.
 *
 * 활성 상태를 기준으로 [Light]는 라이트 모드에서 검정 배경과 흰 글자를 사용하고,
 * [Dark]는 다크 모드에서 흰 배경과 검정 글자를 사용한다.
 *
 * Figma 노드 번호 439-10164, 439-10169.
 */
enum class HilitButtonType { Dark, Light }

@Composable
fun HilitFixedBottomButton(
    modifier: Modifier = Modifier,
    text: String = "",
    enabled: Boolean = true,
    type: HilitButtonType = HilitButtonType.Light,
    onClick: () -> Unit,
) {
    val colors = HilitTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val color = hilitFixedBottomButtonColors(type = type, enabled = enabled, colors = colors)

    val backgroundColor =
        when {
            pressed -> color.pressColor
            else -> color.backgroundColor
        }

    Box(
        modifier =
            modifier
                .background(backgroundColor)
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HilitTheme.typography.sub7,
            color = color.contentColor,
        )
    }
}

internal fun hilitFixedBottomButtonColors(
    type: HilitButtonType,
    enabled: Boolean,
    colors: HilitColors,
): BtnColorSet =
    when (type) {
        HilitButtonType.Light if enabled -> {
            BtnColorSet(
                contentColor = colors.hilitWhite,
                backgroundColor = colors.hilitBlack800,
                pressColor = colors.gray900,
            )
        }

        HilitButtonType.Dark if enabled -> {
            BtnColorSet(
                contentColor = colors.hilitBlack800,
                backgroundColor = colors.hilitWhite,
                pressColor = colors.gray100,
            )
        }

        HilitButtonType.Light -> {
            BtnColorSet(
                contentColor = colors.gray300,
                backgroundColor = colors.gray50,
                pressColor = colors.gray50,
            )
        }

        else -> {
            BtnColorSet(
                contentColor = colors.gray300,
                backgroundColor = colors.hilitWhite,
                pressColor = colors.hilitWhite,
            )
        }
    }

@Preview(name = "HilitFixedBottomButton")
@Composable
private fun HilitFixedBottomButtonPreview() {
    HilitTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(HilitTheme.colors.gray500),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HilitFixedBottomButton(
                    modifier = Modifier,
                    text = "라이트 모드 · 활성",
                    type = HilitButtonType.Light,
                    enabled = true,
                    onClick = {},
                )
                HilitFixedBottomButton(
                    modifier = Modifier,
                    text = "라이트 모드 · 비활성",
                    type = HilitButtonType.Light,
                    enabled = false,
                    onClick = {},
                )
                HilitFixedBottomButton(
                    modifier = Modifier,
                    text = "다크 모드 · 활성",
                    type = HilitButtonType.Dark,
                    enabled = true,
                    onClick = {},
                )
                HilitFixedBottomButton(
                    modifier = Modifier,
                    text = "다크 모드 · 비활성",
                    type = HilitButtonType.Dark,
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}

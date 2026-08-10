package com.dminus14.designsystem.component.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 텍스트 버튼형 TopBar.
 *
 * 지원 [TopBarType]: [TopBarType.Max], [TopBarType.HideLeft], [TopBarType.HideMiddle].
 * [TopBarType.HideRight]는 Max와 동일하게 처리한다.
 *
 * Figma 노드 번호: 439-10398, 439-10399, 439-10400.
 *
 * @param type 영역 노출 타입
 * @param leftIcon 좌측 아이콘. 기본 cancel
 * @param title 중앙 타이틀. 기본 "타이틀"
 * @param buttonText 우측 미니 버튼 문구. 기본 "버튼"
 * @param onLeftClick 좌측 아이콘 클릭
 * @param onButtonClick 우측 버튼 클릭
 * @param modifier 외부 Modifier
 */
@Composable
fun HilitTextTopBar(
    type: TopBarType = TopBarType.Max,
    leftIcon: HilitIconAsset = HilitIconAsset.Cancel,
    title: String = "타이틀",
    buttonText: String = "버튼",
    onLeftClick: () -> Unit = {},
    onButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val effectiveType =
        when (type) {
            TopBarType.HideRight -> TopBarType.Max
            else -> type
        }
    val showLeft = effectiveType != TopBarType.HideLeft
    val showTitle = effectiveType != TopBarType.HideMiddle

    HilitTopBar(
        modifier = modifier,
        leading =
            if (showLeft) {
                {
                    TopBarIconButton(
                        asset = leftIcon,
                        onClick = onLeftClick,
                        contentDescription = null,
                    )
                }
            } else {
                null
            },
        title =
            if (showTitle) {
                {
                    Text(
                        text = title,
                        style = HilitTheme.typography.sub7,
                        color = HilitTheme.colors.hilitBlack800,
                    )
                }
            } else {
                null
            },
        trailing = {
            HilitMiniButton(
                color = HilitMiniButtonColor.Non,
                onClick = onButtonClick,
            ) {
                Text(
                    text = buttonText,
                    style = HilitTheme.typography.body5,
                )
            }
        },
    )
}

@Preview(name = "HilitTextTopBar", showBackground = true, widthDp = 375)
@Composable
private fun HilitTextTopBarPreview() {
    HilitTheme {
        Column {
            listOf(
                TopBarType.Max,
                TopBarType.HideLeft,
                TopBarType.HideMiddle,
            ).forEach { type ->
                HilitTextTopBar(type = type)
            }
        }
    }
}

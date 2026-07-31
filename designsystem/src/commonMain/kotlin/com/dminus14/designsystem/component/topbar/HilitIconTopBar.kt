package com.dminus14.designsystem.component.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 아이콘형 TopBar.
 *
 * 지원 [TopBarType]: [TopBarType.Max], [TopBarType.HideLeft], [TopBarType.HideMiddle],
 * [TopBarType.HideRight].
 *
 * @param type 영역 노출 타입
 * @param leftIcon 좌측 아이콘. 기본 cancel
 * @param title 중앙 타이틀. 기본 "타이틀"
 * @param rightIcon 우측 아이콘. 기본 plus
 * @param onLeftClick 좌측 아이콘 클릭
 * @param onRightClick 우측 아이콘 클릭
 * @param modifier 외부 Modifier
 */
@Composable
fun HilitIconTopBar(
    type: TopBarType = TopBarType.Max,
    leftIcon: HilitIconAsset = HilitIconAsset.Cancel,
    title: String = "타이틀",
    rightIcon: HilitIconAsset = HilitIconAsset.Plus,
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val showLeft = type != TopBarType.HideLeft
    val showTitle = type != TopBarType.HideMiddle
    val showRight = type != TopBarType.HideRight

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
        trailing =
            if (showRight) {
                {
                    TopBarIconButton(
                        asset = rightIcon,
                        onClick = onRightClick,
                        contentDescription = null,
                    )
                }
            } else {
                null
            },
    )
}

@Preview(name = "HilitIconTopBar", showBackground = true, widthDp = 375)
@Composable
private fun HilitIconTopBarPreview() {
    HilitTheme {
        Column {
            TopBarType.entries.forEach { type ->
                HilitIconTopBar(type = type)
            }
        }
    }
}

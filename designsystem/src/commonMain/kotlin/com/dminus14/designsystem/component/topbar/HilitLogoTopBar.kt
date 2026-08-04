package com.dminus14.designsystem.component.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 로고형 TopBar.
 *
 * 좌측 Hilit 로고는 고정이다. 타이틀 슬롯은 사용하지 않는다.
 *
 * 지원 [TopBarType]: [TopBarType.Max], [TopBarType.HideRight].
 * [TopBarType.HideLeft] / [TopBarType.HideMiddle]은 Max와 동일하게 처리한다.
 *
 * @param type 영역 노출 타입
 * @param rightIcon 우측 아이콘. null이면 우측을 비운다. 기본 profile
 * @param onRightClick 우측 아이콘 클릭
 * @param modifier 외부 Modifier
 */
@Composable
fun HilitLogoTopBar(
    type: TopBarType = TopBarType.Max,
    rightIcon: HilitIconAsset? = HilitIconAsset.Profile,
    onRightClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val showRight = type != TopBarType.HideRight && rightIcon != null

    HilitTopBar(
        modifier = modifier,
        leading = {
            HilitIcon(
                asset = HilitIconAsset.HilitLogo,
                contentDescription = null,
                modifier =
                    Modifier
                        .width(HilitLogoWidth)
                        .height(HilitLogoHeight),
            )
        },
        title = null,
        trailing =
            if (showRight) {
                {
                    TopBarIconButton(
                        asset = requireNotNull(rightIcon),
                        onClick = onRightClick,
                        contentDescription = null,
                    )
                }
            } else {
                null
            },
    )
}

private val HilitLogoWidth = 57.dp
private val HilitLogoHeight = 24.dp

@Preview(name = "HilitLogoTopBar", showBackground = true, widthDp = 375)
@Composable
private fun HilitLogoTopBarPreview() {
    HilitTheme {
        Column {
            HilitLogoTopBar(type = TopBarType.Max)
            HilitLogoTopBar(type = TopBarType.HideRight)
        }
    }
}

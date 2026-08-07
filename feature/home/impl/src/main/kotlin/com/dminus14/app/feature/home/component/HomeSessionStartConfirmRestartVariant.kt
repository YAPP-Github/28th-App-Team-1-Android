package com.dminus14.app.feature.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val IconSize = 48.dp
private val IconToLabelSpacing = 16.dp
private val LabelToDescriptionSpacing = 8.dp
private val DescriptionToInfoFieldSpacing = 16.dp

/**
 * Figma 443:5873 — "처음부터 시작"을 다시 한 번 확인하는 오버레이.
 *
 * 말풍선+물음표 아이콘이 없어 임시로 `HilitIconAsset.Script`로 표시.
 */
@Composable
internal fun HomeSessionStartConfirmRestartVariant(
    callbacks: HomeSessionStartCallbacks,
    modifier: Modifier = Modifier,
) {
    HomeSessionStartScaffold(
        title = "처음부터\n시작하시겠어요?",
        onCloseClick = callbacks.onCloseClick,
        card = {
            HomeSessionStartCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HilitIcon(
                        asset = HilitIconAsset.Script,
                        contentDescription = null,
                        tint = HilitTheme.colors.hilitGreen800,
                        modifier = Modifier.size(IconSize),
                    )
                    Text(
                        text = "처음부터 시작하면",
                        style = HilitTheme.typography.body9,
                        color = HilitTheme.colors.gray500,
                        modifier = Modifier.padding(top = IconToLabelSpacing),
                    )
                    Text(
                        text = "지금까지 진행한 면접 내용으로\n레포트가 제작돼요",
                        style = HilitTheme.typography.sub7,
                        color = HilitTheme.colors.hilitBlack800,
                        modifier = Modifier.padding(top = LabelToDescriptionSpacing),
                        textAlign = TextAlign.Center,
                    )
                    HomeSessionStartInfoField(
                        text = "이용권이 하나 차감됩니다.",
                        modifier = Modifier.padding(top = DescriptionToInfoFieldSpacing),
                    )
                }
            }
        },
        bottomButton = {
            HilitFixedBottomDualButton(
                leftText = "뒤로가기",
                rightText = "처음부터 시작",
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = callbacks.onBackClick,
                onRightClick = callbacks.onRestartClick,
            )
        },
        modifier = modifier,
    )
}

@Preview(name = "ConfirmRestart variant", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeSessionStartConfirmRestartVariantPreview() {
    HilitTheme {
        HomeSessionStartConfirmRestartVariant(callbacks = HomeSessionStartCallbacks())
    }
}

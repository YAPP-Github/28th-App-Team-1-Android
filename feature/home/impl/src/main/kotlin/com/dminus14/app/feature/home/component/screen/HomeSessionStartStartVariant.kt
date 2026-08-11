package com.dminus14.app.feature.home.component.screen

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
import com.dminus14.app.feature.home.HomeSessionStartCallbacks
import com.dminus14.app.feature.home.HomeSessionStartOverlayState
import com.dminus14.app.feature.home.component.HomeSessionStartCard
import com.dminus14.app.feature.home.component.HomeSessionStartScaffold
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val IconSize = 74.dp
private val LabelToValueSpacing = 8.dp
private val IconToTextSpacing = 16.dp

/**
 * Figma 443:5821 — 이용권이 남은 사용자에게 면접 시작을 제안하는 오버레이.
 *
 * 러너(뛰는 사람) 아이콘이 designsystem에 아직 없어 임시로 `HilitIconAsset.Play`로 표시한다.
 * 아이콘 SVG 조달 후 `HilitIconAsset`에 러너 엔트리 추가하고 교체하면 된다.
 */
@Composable
internal fun HomeSessionStartStartVariant(
    state: HomeSessionStartOverlayState.Start,
    callbacks: HomeSessionStartCallbacks,
    modifier: Modifier = Modifier,
) {
    HomeSessionStartScaffold(
        title = "${state.userName}님,\n지금부터 면접을\n시작해 볼까요?",
        onCloseClick = callbacks.onCloseClick,
        card = {
            HomeSessionStartCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HilitIcon(
                        asset = HilitIconAsset.OppX,
                        contentDescription = null,
                        tint = HilitTheme.colors.hilitGreen800,
                        modifier = Modifier.size(IconSize),
                    )
                    Text(
                        text = "남은 면접 기회",
                        style = HilitTheme.typography.body9,
                        color = HilitTheme.colors.gray500,
                        modifier = Modifier.padding(top = IconToTextSpacing),
                    )
                    Text(
                        text = "${state.remainingTicketCount}회",
                        style = HilitTheme.typography.sub7,
                        color = HilitTheme.colors.hilitBlack800,
                        modifier = Modifier.padding(top = LabelToValueSpacing),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        bottomButton = {
            HilitFixedBottomButton(
                text = "시작하기",
                type = HilitButtonType.Light,
                onClick = callbacks.onStartClick,
            )
        },
        modifier = modifier,
    )
}

@Preview(name = "Start variant", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeSessionStartStartVariantPreview() {
    HilitTheme {
        HomeSessionStartStartVariant(
            state = HomeSessionStartOverlayState.Start(userName = "재원", remainingTicketCount = 3),
            callbacks = HomeSessionStartCallbacks(),
        )
    }
}

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
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val IconSize = 48.dp
private val LabelToValueSpacing = 8.dp
private val IconToTextSpacing = 16.dp

/**
 * Figma 443:5857 — 무료 이용권을 모두 소진한 상태에서 표시하는 오버레이.
 *
 * "슬픈 러너" 아이콘이 designsystem에 없어 임시로 `HilitIconAsset.Pause`로 표시한다.
 * SVG 조달 후 `HilitIconAsset`에 slot 추가 → 교체.
 */
@Composable
internal fun HomeSessionStartNoTicketsVariant(
    state: HomeSessionStartOverlayState.NoTickets,
    callbacks: HomeSessionStartCallbacks,
    modifier: Modifier = Modifier,
) {
    HomeSessionStartScaffold(
        title = "${state.userName}님,\n무료 횟수를 모두\n사용했어요",
        onCloseClick = callbacks.onCloseClick,
        card = {
            HomeSessionStartCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HilitIcon(
                        asset = HilitIconAsset.Pause,
                        contentDescription = null,
                        tint = HilitTheme.colors.gray400,
                        modifier = Modifier.size(IconSize),
                    )
                    Text(
                        text = "남은 면접 기회",
                        style = HilitTheme.typography.body9,
                        color = HilitTheme.colors.gray500,
                        modifier = Modifier.padding(top = IconToTextSpacing),
                    )
                    Text(
                        text = "0회",
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
                text = "홈으로",
                type = HilitButtonType.Light,
                onClick = callbacks.onGoHomeClick,
            )
        },
        modifier = modifier,
    )
}

@Preview(name = "NoTickets variant", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeSessionStartNoTicketsVariantPreview() {
    HilitTheme {
        HomeSessionStartNoTicketsVariant(
            state = HomeSessionStartOverlayState.NoTickets(userName = "재원"),
            callbacks = HomeSessionStartCallbacks(),
        )
    }
}

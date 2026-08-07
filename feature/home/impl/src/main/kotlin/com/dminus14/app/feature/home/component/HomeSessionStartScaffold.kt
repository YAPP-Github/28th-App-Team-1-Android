package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.text.GradientText
import com.dminus14.designsystem.theme.HilitTheme

private val ClosePadding = 24.dp
private val TitleHorizontalPadding = 20.dp
private val TitleTopSpacing = 20.dp
private val CardHorizontalPadding = 20.dp

/**
 * 세션 시작 오버레이 5종이 공유하는 스캐폴드.
 *
 * 위→아래: 좌상단 X(닫기), 좌 정렬 GradientText 헤딩, 중앙 카드 슬롯, 화면 폭을 채우는 하단 버튼 슬롯.
 * 배경은 부드러운 세로 초록 그라디언트(top green → white).
 */
@Composable
internal fun HomeSessionStartScaffold(
    title: String,
    card: @Composable () -> Unit,
    bottomButton: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundBrush =
        Brush.verticalGradient(
            colors =
                listOf(
                    HilitTheme.colors.hilitGreen500,
                    HilitTheme.colors.hilitWhite,
                ),
        )

    Box(modifier = modifier.fillMaxSize().background(backgroundBrush)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeSessionStartCloseIcon(onClick = onCloseClick)

            GradientText(
                text = title,
                modifier =
                    Modifier
                        .padding(horizontal = TitleHorizontalPadding)
                        .padding(top = TitleTopSpacing),
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = CardHorizontalPadding),
                contentAlignment = Alignment.Center,
            ) {
                card()
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                bottomButton()
            }
        }
    }
}

@Composable
private fun HomeSessionStartCloseIcon(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    HilitIcon(
        asset = HilitIconAsset.Cancel,
        contentDescription = null,
        tint = HilitTheme.colors.hilitBlack800,
        modifier =
            Modifier
                .padding(top = ClosePadding, start = ClosePadding)
                .size(HilitIconAsset.Cancel.defaultSize)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
    )
}

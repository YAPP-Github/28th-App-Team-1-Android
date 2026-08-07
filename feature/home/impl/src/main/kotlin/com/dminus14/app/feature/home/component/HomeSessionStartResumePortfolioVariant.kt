package com.dminus14.app.feature.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.theme.HilitTheme

private val LabelBottomSpacing = 16.dp

/**
 * Figma 443:5839 — 이전에 등록한 포트폴리오로 이어서 시작할지 확인하는 오버레이.
 */
@Composable
internal fun HomeSessionStartResumePortfolioVariant(
    state: HomeSessionStartOverlayState.ResumePortfolio,
    callbacks: HomeSessionStartCallbacks,
    modifier: Modifier = Modifier,
) {
    HomeSessionStartScaffold(
        title = "이전과 동일한 정보로\n시작할까요?",
        onCloseClick = callbacks.onCloseClick,
        card = {
            HomeSessionStartCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "등록한 포트폴리오",
                        style = HilitTheme.typography.sub7,
                        color = HilitTheme.colors.hilitBlack800,
                        modifier = Modifier.padding(bottom = LabelBottomSpacing),
                    )
                    HomeSessionStartPdfRow(
                        fileName = state.fileName,
                        subText = "${state.uploadedAt} | ${state.sizeText}",
                    )
                }
            }
        },
        bottomButton = {
            HilitFixedBottomDualButton(
                leftText = "수정하기",
                rightText = "시작하기",
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = callbacks.onEditPortfolioClick,
                onRightClick = callbacks.onStartClick,
            )
        },
        modifier = modifier,
    )
}

@Preview(name = "ResumePortfolio variant", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeSessionStartResumePortfolioVariantPreview() {
    HilitTheme {
        HomeSessionStartResumePortfolioVariant(
            state =
                HomeSessionStartOverlayState.ResumePortfolio(
                    fileName = "{파일명}.pdf",
                    uploadedAt = "20xx.xx.xx",
                    sizeText = "{0}mb",
                ),
            callbacks = HomeSessionStartCallbacks(),
        )
    }
}

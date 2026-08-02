package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val ModalMaxWidth = 327.dp
private val ModalContentPadding = 24.dp
private val ModalContentPaddingVertical = 40.dp
private val IllustrationSize = 74.dp
private val IllustrationBadgeWidth = 55.dp
private val IllustrationBadgeHeight = 46.dp
private val IllustrationBadgeOffsetX = 17.dp
private val IllustrationIconWidth = 63.dp
private val IllustrationIconHeight = 67.dp
private val IllustrationIconOffsetX = 4.dp
private val IllustrationIconOffsetY = 10.dp
private val IllustrationToTitleSpacing = 20.dp

/**
 * "기존에 있는 포트폴리오로 진행할까요?" 확인 모달(Figma `443:9623`).
 *
 * [com.dminus14.designsystem.component.modal.HilitModal]은 듀얼 버튼일 때 항상
 * [HilitFixedBottomDualButtonType.TwoColor]로 그리는데, 이 모달은 두 버튼 모두 검정 배경
 * (Default)이라 재사용하지 않고 같은 [Dialog] 기반으로 직접 구성했다.
 */
@Composable
fun OnBoardingExistingPortfolioModal(
    onUseExistingClick: () -> Unit,
    onUploadNewClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        OnBoardingExistingPortfolioModalContent(
            onUseExistingClick = onUseExistingClick,
            onUploadNewClick = onUploadNewClick,
        )
    }
}

@Composable
private fun OnBoardingExistingPortfolioModalContent(
    onUseExistingClick: () -> Unit,
    onUploadNewClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .widthIn(max = ModalMaxWidth)
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitWhite),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ModalContentPadding,
                        vertical = ModalContentPaddingVertical,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OnBoardingExistingPortfolioIllustration()

            Text(
                text = "기존에 있는 포트폴리오로\n진행할까요?",
                modifier =
                    Modifier
                        .padding(top = IllustrationToTitleSpacing)
                        .fillMaxWidth(),
                color = HilitTheme.colors.gray900,
                style = HilitTheme.typography.sub4,
                textAlign = TextAlign.Center,
            )
        }

        HilitFixedBottomDualButton(
            leftText = "새로 업로드",
            rightText = "기존 포트폴리오 사용",
            type = HilitFixedBottomDualButtonType.Default,
            onLeftClick = onUploadNewClick,
            onRightClick = onUseExistingClick,
        )
    }
}

/** book/74px(Figma `435:487`): 초록 배지 위에 책 라인아트를 겹친 일러스트. */
@Composable
private fun OnBoardingExistingPortfolioIllustration() {
    Box(modifier = Modifier.size(IllustrationSize)) {
        Box(
            modifier =
                Modifier
                    .offset(x = IllustrationBadgeOffsetX)
                    .size(width = IllustrationBadgeWidth, height = IllustrationBadgeHeight)
                    .background(HilitTheme.colors.hilitGreen500),
        )

        HilitIcon(
            asset = HilitIconAsset.Book,
            contentDescription = null,
            modifier =
                Modifier
                    .offset(x = IllustrationIconOffsetX, y = IllustrationIconOffsetY)
                    .size(width = IllustrationIconWidth, height = IllustrationIconHeight),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun OnBoardingExistingPortfolioModalContentPreview() {
    HilitTheme {
        OnBoardingExistingPortfolioModalContent(
            onUseExistingClick = {},
            onUploadNewClick = {},
        )
    }
}

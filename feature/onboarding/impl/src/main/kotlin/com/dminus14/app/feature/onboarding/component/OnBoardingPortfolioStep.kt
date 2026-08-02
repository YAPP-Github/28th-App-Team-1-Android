package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.OnBoardingInterviewIntent
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.fileupload.FileUploadGuide
import com.dminus14.designsystem.component.fileupload.PdfUpload
import com.dminus14.designsystem.component.fileupload.PdfUploadType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.modal.HilitBookIllustration
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalType
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

private val SubtitleToLabelSpacing = 30.dp
private val LabelToUploadSpacing = 10.dp
private val UploadToStatusSpacing = 10.dp
private val RequiredErrorBorderWidth = 1.2.dp
private val RequiredErrorHorizontalPadding = 14.dp
private val RequiredErrorVerticalPadding = 12.dp
private val RequiredErrorIconSize = 16.dp
private val RequiredErrorIconTextGap = 8.dp

@Composable
fun OnBoardingPortfolioStep(
    fileName: String?,
    showExistingPortfolioModal: Boolean,
    showRequiredError: Boolean,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OnBoardingStepHeader(
            tagText = "필수",
            tagColorType = TagColorType.Black,
            title =
                buildAnnotatedString {
                    withHilitTextHighlight { append("포트폴리오") }
                    append("를 \n업로드해 주세요.")
                },
            subtitle = "포트폴리오를 분석해 면접 질문이 나와요.",
        )

        Column(modifier = Modifier.padding(top = SubtitleToLabelSpacing)) {
            Text(
                text = "업로드한 포트폴리오",
                style = HilitTheme.typography.body2,
                color = HilitTheme.colors.hilitBlack800,
                modifier = Modifier.fillMaxWidth(),
            )

            FileUploadGuide(
                onClick = { onIntent(OnBoardingInterviewIntent.PortfolioUploadClick) },
                modifier = Modifier.padding(top = LabelToUploadSpacing),
            )

            if (showRequiredError) {
                OnBoardingPortfolioRequiredError(
                    modifier = Modifier.padding(top = UploadToStatusSpacing),
                )
            }

            PdfUpload(
                type = if (fileName != null) PdfUploadType.Completed else PdfUploadType.Ready,
                fileName = fileName.orEmpty(),
                onCloseClick =
                    if (fileName != null) {
                        { onIntent(OnBoardingInterviewIntent.PortfolioRemoveClick) }
                    } else {
                        null
                    },
                modifier = Modifier.padding(top = UploadToStatusSpacing),
            )
        }
    }

    if (showExistingPortfolioModal) {
        OnBoardingExistingPortfolioModal(onIntent = onIntent)
    }
}

@Composable
private fun OnBoardingExistingPortfolioModal(onIntent: (OnBoardingInterviewIntent) -> Unit) {
    HilitModal(
        type = HilitModalType.InvisibleInfo,
        title = "기존에 있는 포트폴리오로\n진행할까요?",
        dismissible = false,
        graphic = { HilitBookIllustration() },
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "새로 업로드",
                rightText = "기존 포트폴리오 사용",
                type = HilitFixedBottomDualButtonType.Default,
                onLeftClick = { onIntent(OnBoardingInterviewIntent.PortfolioUploadNewClick) },
                onRightClick = { onIntent(OnBoardingInterviewIntent.PortfolioUseExistingClick) },
            )
        },
    )
}

/** 필수 업로드 없이 계속하기를 눌렀을 때 뜨는 인라인 에러(Figma `443:9641`, "info-field"). */
@Composable
private fun OnBoardingPortfolioRequiredError(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = HilitTheme.colors.error200, shape = RectangleShape)
                .border(
                    width = RequiredErrorBorderWidth,
                    color = HilitTheme.colors.error300,
                    shape = RectangleShape,
                ).padding(
                    horizontal = RequiredErrorHorizontalPadding,
                    vertical = RequiredErrorVerticalPadding,
                ),
        horizontalArrangement = Arrangement.spacedBy(RequiredErrorIconTextGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = HilitIconAsset.FillWarning,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(RequiredErrorIconSize),
        )
        Text(
            text = "포트폴리오를 업로드해주세요",
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.error500,
        )
    }
}

@Preview(name = "Empty", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPortfolioStepEmptyPreview() {
    HilitTheme {
        OnBoardingPortfolioStep(
            fileName = null,
            showExistingPortfolioModal = false,
            showRequiredError = false,
            onIntent = {},
        )
    }
}

@Preview(name = "Uploaded", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPortfolioStepUploadedPreview() {
    HilitTheme {
        OnBoardingPortfolioStep(
            fileName = "포트폴리오.pdf",
            showExistingPortfolioModal = false,
            showRequiredError = false,
            onIntent = {},
        )
    }
}

@Preview(name = "Required error", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPortfolioStepRequiredErrorPreview() {
    HilitTheme {
        OnBoardingPortfolioStep(
            fileName = null,
            showExistingPortfolioModal = false,
            showRequiredError = true,
            onIntent = {},
        )
    }
}

@Preview(name = "Existing portfolio modal", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPortfolioStepModalPreview() {
    HilitTheme {
        OnBoardingPortfolioStep(
            fileName = null,
            showExistingPortfolioModal = true,
            showRequiredError = false,
            onIntent = {},
        )
    }
}

/**
 * 업로드 중(Processing)은 실제 파일 업로드 연동 전이라 프로덕션 시그니처(`fileName: String?`)로는
 * 표현할 수 없어서, 디자인 검수용으로 [PdfUpload]를 직접 조립해 미리보기만 제공한다.
 */
@Preview(name = "Uploading", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPortfolioStepUploadingPreview() {
    HilitTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            OnBoardingStepHeader(
                tagText = "필수",
                tagColorType = TagColorType.Black,
                title =
                    buildAnnotatedString {
                        withHilitTextHighlight { append("포트폴리오") }
                        append("를 \n업로드해 주세요.")
                    },
                subtitle = "포트폴리오를 분석해 면접 질문이 나와요.",
            )

            Column(modifier = Modifier.padding(top = SubtitleToLabelSpacing)) {
                Text(
                    text = "업로드한 포트폴리오",
                    style = HilitTheme.typography.body2,
                    color = HilitTheme.colors.hilitBlack800,
                    modifier = Modifier.fillMaxWidth(),
                )

                FileUploadGuide(
                    onClick = {},
                    modifier = Modifier.padding(top = LabelToUploadSpacing),
                )

                PdfUpload(
                    type = PdfUploadType.Processing,
                    fileName = "포트폴리오.pdf",
                    onCloseClick = {},
                    modifier = Modifier.padding(top = UploadToStatusSpacing),
                )
            }
        }
    }
}

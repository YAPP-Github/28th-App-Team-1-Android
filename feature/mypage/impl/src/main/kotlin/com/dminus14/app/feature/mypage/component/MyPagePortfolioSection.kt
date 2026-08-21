@file:Suppress("TooManyFunctions")

package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.mypage.MyPagePortfolioState
import com.dminus14.app.feature.mypage.MyPagePortfolioUiModel
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType
import com.dminus14.designsystem.component.fileupload.FileUploadGuide
import com.dminus14.designsystem.component.fileupload.PdfUpload
import com.dminus14.designsystem.component.fileupload.PdfUploadType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
@Suppress("LongMethod", "LongParameterList")
internal fun MyPagePortfolioSection(
    portfolioState: MyPagePortfolioState,
    isUploadFailureTooltipVisible: Boolean,
    onUploadClick: () -> Unit,
    onUploadCancelClick: () -> Unit,
    onUploadRetryClick: () -> Unit,
    onUploadFailureInfoClick: () -> Unit,
    onUploadFailureTooltipDismiss: () -> Unit,
    onPortfolioDeleteClick: () -> Unit,
    onPortfolioReuploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isUploadFailureTooltipVisible) {
        if (isUploadFailureTooltipVisible) {
            delay(UPLOAD_FAILURE_TOOLTIP_DURATION_MS.milliseconds)
            onUploadFailureTooltipDismiss()
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("내 포트폴리오", style = HilitTheme.typography.body6, color = HilitTheme.colors.gray500)
        when (portfolioState) {
            MyPagePortfolioState.Empty -> {
                FileUploadGuide(
                    title = "포트폴리오를 업로드해주세요",
                    description = UPLOAD_CONSTRAINT_TEXT,
                    onClick = onUploadClick,
                )
                PdfUpload(
                    type = PdfUploadType.Ready,
                    progress = 0f,
                )
            }

            is MyPagePortfolioState.Uploaded -> {
                UploadedPortfolioCard(
                    portfolio = portfolioState.portfolio,
                    onDeleteClick = onPortfolioDeleteClick,
                    onReuploadClick = onPortfolioReuploadClick,
                )
            }

            is MyPagePortfolioState.Uploading -> {
                PdfUpload(
                    type = PdfUploadType.Processing,
                    fileName = portfolioState.fileName,
                    progress = portfolioState.progress,
                    onCloseClick = onUploadCancelClick,
                )
            }

            is MyPagePortfolioState.Completed -> {
                PdfUpload(
                    type = PdfUploadType.Completed,
                    fileName = portfolioState.fileName,
                    progress = 100f,
                    onCloseClick = onPortfolioDeleteClick,
                )
            }

            is MyPagePortfolioState.Failed -> {
                Box {
                    PdfUpload(
                        type = PdfUploadType.Failed,
                        fileName = portfolioState.fileName,
                        progress = 0f,
                        onInfoClick = onUploadFailureInfoClick,
                        onRetryClick = onUploadRetryClick,
                    )
                    if (isUploadFailureTooltipVisible) {
                        BubbleField(
                            text = "아직 다시 업로드할 수 있어요!",
                            type = BubbleFieldType.Small,
                            tailEdge = BubbleFieldTailEdge.Bottom,
                            tailAlign = BubbleFieldTailAlign.Left,
                            tailShape = BubbleFieldTailShape.Left,
                            modifier =
                                Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 89.dp),
                        )
                    }
                }
                UploadErrorNotice()
            }
        }
        if (
            portfolioState !is MyPagePortfolioState.Failed &&
            portfolioState !is MyPagePortfolioState.Empty
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(HilitTheme.colors.gray100)
                        .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Info,
                    contentDescription = null,
                    tint = HilitTheme.colors.hilitBlack800,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "포트폴리오는 한 달에 한 번 바꿀 수 있어요. 지워도 지난 면접 리포트는 그대로 남아요.",
                    style = HilitTheme.typography.body9,
                    color = HilitTheme.colors.gray700,
                )
            }
        }
    }
}

@Composable
private fun UploadedPortfolioCard(
    portfolio: MyPagePortfolioUiModel,
    onDeleteClick: () -> Unit,
    onReuploadClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitWhite)
                .border(1.5.dp, HilitTheme.colors.gray100)
                .clickable(role = Role.Button, onClick = onReuploadClick)
                .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier
                .size(36.dp)
                .background(HilitTheme.colors.hilitBlack800),
            contentAlignment = Alignment.Center,
        ) {
            HilitIcon(
                asset = HilitIconAsset.File,
                contentDescription = null,
                tint = HilitTheme.colors.hilitGreen500,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                portfolio.fileName,
                style = HilitTheme.typography.body2,
                color = HilitTheme.colors.gray700,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "${portfolio.uploadedAt} | ${portfolio.fileSize}",
                style = HilitTheme.typography.body9,
                color = HilitTheme.colors.gray400,
            )
        }
        HilitIcon(
            asset = HilitIconAsset.Delete,
            contentDescription = "포트폴리오 삭제",
            modifier =
                Modifier
                    .size(16.dp)
                    .clickable(role = Role.Button, onClick = onDeleteClick),
        )
    }
}

@Composable
private fun UploadErrorNotice() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.error200)
                .border(1.2.dp, HilitTheme.colors.error300)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = HilitIconAsset.FillWarning,
            contentDescription = null,
            tint = HilitTheme.colors.error500,
            modifier = Modifier.size(16.dp),
        )
        Text(
            UPLOAD_CONSTRAINT_TEXT,
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.error500,
        )
    }
}

private const val UPLOAD_CONSTRAINT_TEXT = "최대 20MB, 30쪽 이내의 PDF 파일 1개만 가능해요"
private const val UPLOAD_FAILURE_TOOLTIP_DURATION_MS = 3_000L

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPagePortfolioSectionPreview() {
    HilitTheme {
        MyPagePortfolioSection(
            portfolioState = MyPagePortfolioState.Failed(fileName = "sample_portfolio.pdf"),
            isUploadFailureTooltipVisible = true,
            onUploadClick = {},
            onUploadCancelClick = {},
            onUploadRetryClick = {},
            onUploadFailureInfoClick = {},
            onUploadFailureTooltipDismiss = {},
            onPortfolioDeleteClick = {},
            onPortfolioReuploadClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPagePortfolioSectionEmptyPreview() {
    HilitTheme {
        MyPagePortfolioSection(
            portfolioState = MyPagePortfolioState.Empty,
            isUploadFailureTooltipVisible = false,
            onUploadClick = {},
            onUploadCancelClick = {},
            onUploadRetryClick = {},
            onUploadFailureInfoClick = {},
            onUploadFailureTooltipDismiss = {},
            onPortfolioDeleteClick = {},
            onPortfolioReuploadClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPagePortfolioSectionSuccessPreview() {
    HilitTheme {
        MyPagePortfolioSection(
            portfolioState =
                MyPagePortfolioState.Uploaded(
                    portfolio =
                        MyPagePortfolioUiModel(
                            fileName = "portfolio.pdf",
                            uploadedAt = "2000.01.01 00:00",
                            fileSize = "20.3 MB",
                        ),
                ),
            isUploadFailureTooltipVisible = true,
            onUploadClick = {},
            onUploadCancelClick = {},
            onUploadRetryClick = {},
            onUploadFailureInfoClick = {},
            onUploadFailureTooltipDismiss = {},
            onPortfolioDeleteClick = {},
            onPortfolioReuploadClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPagePortfolioSectionUploadingBeforeAcceptedPreview() {
    HilitTheme {
        MyPagePortfolioSection(
            portfolioState =
                MyPagePortfolioState.Uploading(
                    fileName = "portfolio.pdf",
                    progress = 20f,
                ),
            isUploadFailureTooltipVisible = false,
            onUploadClick = {},
            onUploadCancelClick = {},
            onUploadRetryClick = {},
            onUploadFailureInfoClick = {},
            onUploadFailureTooltipDismiss = {},
            onPortfolioDeleteClick = {},
            onPortfolioReuploadClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPagePortfolioSectionUploadingAfterAcceptedPreview() {
    HilitTheme {
        MyPagePortfolioSection(
            portfolioState =
                MyPagePortfolioState.Uploading(
                    fileName = "portfolio.pdf",
                    progress = 60f,
                    portfolioId = "portfolio-1",
                ),
            isUploadFailureTooltipVisible = false,
            onUploadClick = {},
            onUploadCancelClick = {},
            onUploadRetryClick = {},
            onUploadFailureInfoClick = {},
            onUploadFailureTooltipDismiss = {},
            onPortfolioDeleteClick = {},
            onPortfolioReuploadClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPagePortfolioSectionCompletedPreview() {
    HilitTheme {
        MyPagePortfolioSection(
            portfolioState = MyPagePortfolioState.Completed(fileName = "portfolio.pdf"),
            isUploadFailureTooltipVisible = false,
            onUploadClick = {},
            onUploadCancelClick = {},
            onUploadRetryClick = {},
            onUploadFailureInfoClick = {},
            onUploadFailureTooltipDismiss = {},
            onPortfolioDeleteClick = {},
            onPortfolioReuploadClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun UploadedPortfolioCardPreview() {
    HilitTheme {
        UploadedPortfolioCard(
            portfolio =
                MyPagePortfolioUiModel(
                    fileName = "sample_portfolio.pdf",
                    uploadedAt = "2026.08.04",
                    fileSize = "2.4MB",
                ),
            onDeleteClick = {},
            onReuploadClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun UploadErrorNoticePreview() {
    HilitTheme {
        UploadErrorNotice()
    }
}

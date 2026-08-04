package com.dminus14.designsystem.component.fileupload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

enum class PdfUploadType {
    Ready,
    Processing,
    Completed,
    Failed,
}

private val PdfUploadShape = RectangleShape
private val PdfUploadHorizontalPadding = 14.dp
private val PdfUploadVerticalPadding = 14.dp
private val PdfUploadContentSpacing = 12.dp
private val PdfUploadTextSpacing = 4.dp
private val PdfUploadIconSize = 36.dp
private val PdfUploadFileIconSize = 20.dp
private val PdfUploadCloseSize = 16.dp
private val PdfUploadCloseSpacing = 8.dp
private val PdfUploadButtonIconSize = 16.dp
private val PdfUploadButtonPadding = 8.dp
private val PdfUploadButtonSpacing = 8.dp
private val PdfUploadProgressHeight = 4.dp
private val PdfUploadBorderWidth = 1.5.dp
private val PdfUploadReadyMinHeight = 64.dp
private val PdfUploadDashOn = 6.dp
private val PdfUploadDashOff = 4.dp

private const val PDF_UPLOAD_READY_DEFAULT_TEXT = "아직 첨부된 포트폴리오가 없어요"
private const val PDF_UPLOAD_BUTTON_DEFAULT_TEXT = "버튼"
private const val PDF_UPLOAD_PROGRESS_MIN = 0f
private const val PDF_UPLOAD_PROGRESS_MAX = 100f

/**
 * PDF 업로드 상태 표시 영역.
 *
 * Figma: FileUpload status=empty / processing / completed (`443:9714`)
 *
 * @param type Ready(미첨부) / Processing / Completed / Failed
 * @param modifier 외부 레이아웃 Modifier
 * @param fileName Processing·Completed에서 표시할 파일명
 * @param progress Processing 진행도(0f~100f). 범위를 벗어나면 최솟값·최댓값으로 보정한다
 * @param onCloseClick 닫기(제거) 클릭. null이면 닫기 아이콘을 표시하지 않는다
 * @param buttonText Completed 우측 mini 버튼 문구
 * @param onButtonClick Completed 우측 mini 버튼 클릭. null이면 버튼을 표시하지 않는다
 * @param onInfoClick Failed 정보 아이콘 클릭. null이면 아이콘은 비활성으로 표시한다
 * @param retryText Failed 우측 재시도 버튼 문구
 * @param onRetryClick Failed 우측 재시도 버튼 클릭. null이면 버튼을 표시하지 않는다
 */
@Composable
fun PdfUpload(
    type: PdfUploadType,
    modifier: Modifier = Modifier,
    fileName: String = "",
    progress: Float,
    onCloseClick: (() -> Unit)? = null,
    buttonText: String = PDF_UPLOAD_BUTTON_DEFAULT_TEXT,
    onButtonClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null,
    retryText: String = "다시 올리기",
    onRetryClick: (() -> Unit)? = null,
) {
    when (type) {
        PdfUploadType.Ready -> {
            PdfUploadReady(modifier = modifier)
        }

        PdfUploadType.Processing,
        PdfUploadType.Completed,
        PdfUploadType.Failed,
        -> {
            PdfUploadFilled(
                fileName = fileName,
                type = type,
                progress = progress,
                onCloseClick = onCloseClick,
                buttonText = buttonText,
                onButtonClick = onButtonClick,
                onInfoClick = onInfoClick,
                retryText = retryText,
                onRetryClick = onRetryClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun PdfUploadReady(modifier: Modifier = Modifier) {
    val borderColor = HilitTheme.colors.gray200

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = PdfUploadReadyMinHeight)
                .background(
                    color = HilitTheme.colors.hilitWhite,
                    shape = PdfUploadShape,
                ).dashedBorder(
                    width = PdfUploadBorderWidth,
                    color = borderColor,
                    dashOn = PdfUploadDashOn,
                    dashOff = PdfUploadDashOff,
                ).padding(
                    horizontal = PdfUploadHorizontalPadding,
                    vertical = PdfUploadVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = PDF_UPLOAD_READY_DEFAULT_TEXT,
            style = HilitTheme.typography.body6,
            color = HilitTheme.colors.gray300,
        )
    }
}

@Composable
private fun PdfUploadFilled(
    fileName: String,
    type: PdfUploadType,
    progress: Float,
    onCloseClick: (() -> Unit)?,
    buttonText: String,
    onButtonClick: (() -> Unit)?,
    onInfoClick: (() -> Unit)?,
    retryText: String,
    onRetryClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val isCompleted = type == PdfUploadType.Completed
    val isFailed = type == PdfUploadType.Failed
    val statusText =
        when {
            isCompleted -> "Completed!"
            isFailed -> "업로드 실패"
            else -> "Processing..."
        }
    val statusColor =
        when {
            isCompleted -> HilitTheme.colors.hilitGreen800
            isFailed -> HilitTheme.colors.error500
            else -> HilitTheme.colors.gray400
        }
    val infoClickHandler = onInfoClick?.takeIf { isFailed }
    val infoTint =
        if (infoClickHandler != null) HilitTheme.colors.error500 else HilitTheme.colors.gray200

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = HilitTheme.colors.hilitWhite,
                    shape = PdfUploadShape,
                ).border(
                    width = PdfUploadBorderWidth,
                    color = HilitTheme.colors.gray100,
                    shape = PdfUploadShape,
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = PdfUploadHorizontalPadding,
                        vertical = PdfUploadVerticalPadding,
                    ),
            horizontalArrangement = Arrangement.spacedBy(PdfUploadContentSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PdfFileBadge()

            // Figma: text + cancel 그룹 (gap 8, items-start)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(PdfUploadCloseSpacing),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PdfUploadTextSpacing),
                ) {
                    Text(
                        text = fileName,
                        style = HilitTheme.typography.body2,
                        color = HilitTheme.colors.gray700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = statusText,
                            style = HilitTheme.typography.body9,
                            color = statusColor,
                        )
                        if (isFailed) {
                            HilitIcon(
                                asset = HilitIconAsset.Info,
                                contentDescription =
                                    if (infoClickHandler !=
                                        null
                                    ) {
                                        "업로드 실패 안내"
                                    } else {
                                        null
                                    },
                                tint = infoTint,
                                modifier =
                                    Modifier
                                        .size(PdfUploadCloseSize)
                                        .then(
                                            if (infoClickHandler != null) {
                                                Modifier.clickable(
                                                    indication = null,
                                                    interactionSource = null,
                                                    onClick = infoClickHandler,
                                                )
                                            } else {
                                                Modifier
                                            },
                                        ),
                            )
                        }
                    }
                }

                if (onCloseClick != null) {
                    HilitIcon(
                        asset = HilitIconAsset.Delete,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier =
                            Modifier
                                .size(PdfUploadCloseSize)
                                .clickable(
                                    indication = null,
                                    interactionSource = null,
                                    onClick = onCloseClick,
                                ),
                    )
                }
            }

            onButtonClick
                ?.takeIf { isCompleted }
                ?.let { onClick ->
                    PdfUploadActionButton(
                        text = buttonText,
                        onClick = onClick,
                    )
                }

            onRetryClick
                ?.takeIf { isFailed }
                ?.let { onClick ->
                    PdfUploadActionButton(
                        text = retryText,
                        icon = HilitIconAsset.Undo,
                        onClick = onClick,
                    )
                }
        }

        if (!isFailed) {
            PdfUploadProgress(
                progress = if (isCompleted) PDF_UPLOAD_PROGRESS_MAX else progress,
            )
        }
    }
}

/** Figma: button-mini (`3052:13145`) — Video 16px + body5, gray100 배경, padding 8 */
@Composable
private fun PdfUploadActionButton(
    text: String,
    icon: HilitIconAsset = HilitIconAsset.Video,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .background(
                    color = HilitTheme.colors.gray100,
                    shape = PdfUploadShape,
                ).clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = onClick,
                ).padding(PdfUploadButtonPadding),
        horizontalArrangement = Arrangement.spacedBy(PdfUploadButtonSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = icon,
            contentDescription = null,
            tint = HilitTheme.colors.hilitBlack800,
            modifier = Modifier.size(PdfUploadButtonIconSize),
        )
        Text(
            text = text,
            style = HilitTheme.typography.body5,
            color = HilitTheme.colors.hilitBlack800,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Figma: file/36px/green (`3368:11286`)
 * 36dp 검정 배경 + 20dp File 아이콘(hilitGreen500)
 */
@Composable
private fun PdfFileBadge() {
    Box(
        modifier =
            Modifier
                .size(PdfUploadIconSize)
                .background(
                    color = HilitTheme.colors.hilitBlack800,
                    shape = PdfUploadShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        HilitIcon(
            asset = HilitIconAsset.File,
            contentDescription = null,
            tint = HilitTheme.colors.hilitGreen500,
            modifier = Modifier.size(PdfUploadFileIconSize),
        )
    }
}

@Composable
private fun PdfUploadProgress(progress: Float) {
    val progressFraction =
        progress.coerceIn(PDF_UPLOAD_PROGRESS_MIN, PDF_UPLOAD_PROGRESS_MAX) /
            PDF_UPLOAD_PROGRESS_MAX

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(PdfUploadProgressHeight)
                .background(HilitTheme.colors.gray200),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .background(HilitTheme.colors.hilitGreen500),
        )
    }
}

private fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    dashOn: Dp,
    dashOff: Dp,
): Modifier =
    drawBehind {
        val strokeWidth = width.toPx()
        val dashPathEffect =
            PathEffect.dashPathEffect(
                floatArrayOf(dashOn.toPx(), dashOff.toPx()),
                0f,
            )
        drawRect(
            color = color,
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size =
                Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth,
                ),
            style =
                Stroke(
                    width = strokeWidth,
                    pathEffect = dashPathEffect,
                ),
        )
    }

@Preview(
    name = "PdfUpload",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun PdfUploadPreview() {
    HilitTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PdfUpload(
                type = PdfUploadType.Ready,
                progress = 0f,
            )
            PdfUpload(
                type = PdfUploadType.Processing,
                fileName = "홍길동 자기소개서_SK프롬티어 기업....pdf",
                progress = 50f,
                onCloseClick = {},
            )
            PdfUpload(
                type = PdfUploadType.Completed,
                fileName = "홍길동 자기소개서_SK프롬티어 기업....pdf",
                progress = 100f,
                onCloseClick = {},
                onButtonClick = {},
            )
            PdfUpload(
                type = PdfUploadType.Failed,
                fileName = "샘플 포트폴리오.pdf",
                progress = 0f,
                onInfoClick = {},
                onRetryClick = {},
            )
        }
    }
}

package com.dminus14.designsystem.component.fileupload

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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

/** Figma processing 인디케이터 너비 (`2280:10276`, 54/335) */
private val PdfUploadIndeterminateSegmentWidth = 54.dp
private const val PDF_UPLOAD_READY_DEFAULT_TEXT = "아직 첨부된 포트폴리오가 없어요"
private const val PDF_UPLOAD_BUTTON_DEFAULT_TEXT = "버튼"
private const val PDF_UPLOAD_INDETERMINATE_ANIM_DURATION_MS = 1_200

/**
 * PDF 업로드 상태 표시 영역.
 *
 * Figma: FileUpload status=empty / processing / completed (`443:9714`)
 *
 * @param type Ready(미첨부) / Processing / Completed
 * @param modifier 외부 레이아웃 Modifier
 * @param fileName Processing·Completed에서 표시할 파일명
 * @param onCloseClick 닫기(제거) 클릭. null이면 닫기 아이콘을 표시하지 않는다
 * @param buttonText Completed 우측 mini 버튼 문구
 * @param onButtonClick Completed 우측 mini 버튼 클릭. null이면 버튼을 표시하지 않는다
 */
@Composable
fun PdfUpload(
    type: PdfUploadType,
    modifier: Modifier = Modifier,
    fileName: String = "",
    onCloseClick: (() -> Unit)? = null,
    buttonText: String = PDF_UPLOAD_BUTTON_DEFAULT_TEXT,
    onButtonClick: (() -> Unit)? = null,
) {
    when (type) {
        PdfUploadType.Ready -> {
            PdfUploadReady(modifier = modifier)
        }

        PdfUploadType.Processing,
        PdfUploadType.Completed,
        -> {
            PdfUploadFilled(
                fileName = fileName,
                type = type,
                onCloseClick = onCloseClick,
                buttonText = buttonText,
                onButtonClick = onButtonClick,
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
    onCloseClick: (() -> Unit)?,
    buttonText: String,
    onButtonClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val isCompleted = type == PdfUploadType.Completed
    val statusText =
        if (isCompleted) {
            "Completed!"
        } else {
            "Processing..."
        }
    val statusColor =
        if (isCompleted) {
            HilitTheme.colors.hilitGreen800
        } else {
            HilitTheme.colors.gray400
        }

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
                        HilitIcon(
                            asset = HilitIconAsset.Info,
                            contentDescription = null,
                            tint = HilitTheme.colors.gray200,
                            modifier =
                                Modifier
                                    .size(PdfUploadCloseSize),
                        )
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
        }

        PdfUploadProgress(isCompleted = isCompleted)
    }
}

/** Figma: button-mini (`3052:13145`) — Video 16px + body5, gray100 배경, padding 8 */
@Composable
private fun PdfUploadActionButton(
    text: String,
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
            asset = HilitIconAsset.Video,
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
private fun PdfUploadProgress(isCompleted: Boolean) {
    if (isCompleted) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(PdfUploadProgressHeight)
                    .background(HilitTheme.colors.hilitGreen500),
        )
        return
    }

    val transition = rememberInfiniteTransition(label = "pdfUploadIndeterminate")
    val offsetFraction by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = PDF_UPLOAD_INDETERMINATE_ANIM_DURATION_MS,
                        easing = FastOutSlowInEasing,
                    ),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pdfUploadIndeterminateOffset",
    )

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(PdfUploadProgressHeight)
                .clipToBounds()
                .background(HilitTheme.colors.gray200),
    ) {
        val segmentWidth =
            PdfUploadIndeterminateSegmentWidth.coerceAtMost(maxWidth)
        val maxOffset = (maxWidth - segmentWidth).coerceAtLeast(0.dp)

        Box(
            modifier =
                Modifier
                    .offset(x = maxOffset * offsetFraction)
                    .width(segmentWidth)
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
            PdfUpload(type = PdfUploadType.Ready)
            PdfUpload(
                type = PdfUploadType.Processing,
                fileName = "홍길동 자기소개서_SK프롬티어 기업....pdf",
                onCloseClick = {},
            )
            PdfUpload(
                type = PdfUploadType.Completed,
                fileName = "홍길동 자기소개서_SK프롬티어 기업....pdf",
                onCloseClick = {},
                onButtonClick = {},
            )
        }
    }
}

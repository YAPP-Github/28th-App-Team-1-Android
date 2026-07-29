package com.dminus14.designsystem.component.fileupload

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.delete
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

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
private val PdfUploadProgressHeight = 4.dp
private val PdfUploadBorderWidth = 1.5.dp
private val PdfUploadReadyMinHeight = 64.dp
private val PdfUploadDashOn = 6.dp
private val PdfUploadDashOff = 4.dp
private const val PdfUploadReadyDefaultText = "아직 첨부된 포트폴리오가 없어요"

/** 폴링 간격(3s)에 맞춰 목표가 바뀌어도 막대가 끊기지 않고 이어지도록 한다. */
private const val PdfUploadProgressAnimDurationMs = 2_800

/**
 * PDF 업로드 상태 표시 영역.
 *
 * Figma: FileUpload status=empty / processing / completed (`2750:18454`)
 *
 * @param type Ready(미첨부) / Processing / Completed
 * @param modifier 외부 레이아웃 Modifier
 * @param fileName Processing·Completed에서 표시할 파일명
 * @param progress Processing 진행률(0f~1f). Completed에서는 무시되고 1f로 표시한다
 * @param onCloseClick 닫기(제거) 클릭. null이면 닫기 아이콘을 표시하지 않는다
 */
@Composable
fun PdfUpload(
    type: PdfUploadType,
    modifier: Modifier = Modifier,
    fileName: String = "",
    progress: Float = 0f,
    onCloseClick: (() -> Unit)? = null,
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
                progress = progress,
                onCloseClick = onCloseClick,
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
                )
                .dashedBorder(
                    width = PdfUploadBorderWidth,
                    color = borderColor,
                    dashOn = PdfUploadDashOn,
                    dashOff = PdfUploadDashOff,
                )
                .padding(
                    horizontal = PdfUploadHorizontalPadding,
                    vertical = PdfUploadVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = PdfUploadReadyDefaultText,
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
    modifier: Modifier = Modifier,
) {
    val isCompleted = type == PdfUploadType.Completed
    val progressValue =
        if (isCompleted) {
            1f
        } else {
            progress.coerceIn(0f, 1f)
        }
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
                )
                .border(
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
                Text(
                    text = statusText,
                    style = HilitTheme.typography.body9,
                    color = statusColor,
                )
            }

            if (onCloseClick != null) {
                Icon(
                    painter = painterResource(resource = Res.drawable.delete),
                    contentDescription = "",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(PdfUploadCloseSize)
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = onCloseClick,
                        ),
                )
            }
        }

        PdfUploadProgress(
            progress = progressValue,
            trackColor =
                if (isCompleted) {
                    HilitTheme.colors.hilitGreen500
                } else {
                    HilitTheme.colors.gray200
                },
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
private fun PdfUploadProgress(
    progress: Float,
    trackColor: Color,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec =
            tween(
                durationMillis = PdfUploadProgressAnimDurationMs,
                easing = FastOutSlowInEasing,
            ),
        label = "pdfUploadProgress",
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(PdfUploadProgressHeight)
                .background(trackColor),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(fraction = animatedProgress)
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
                progress = 0.2f,
                onCloseClick = {},
            )
            PdfUpload(
                type = PdfUploadType.Completed,
                fileName = "홍길동 자기소개서_SK프롬티어 기업....pdf",
                onCloseClick = {},
            )
        }
    }
}

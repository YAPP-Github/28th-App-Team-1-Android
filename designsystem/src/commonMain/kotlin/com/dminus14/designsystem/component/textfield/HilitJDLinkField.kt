package com.dminus14.designsystem.component.textfield

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.subtext.HilitSubText
import com.dminus14.designsystem.component.subtext.HilitSubTextType
import com.dminus14.designsystem.theme.HilitTheme

enum class HilitJDLinkFieldType {
    Ready,
    Focus,
    Edit,
    Processing,
    Complete,
    Error,
}

private val FieldShape = RectangleShape
private val FieldBorderWidth = 1.dp
private val FieldHorizontalPadding = 16.dp
private val FieldVerticalPadding = 14.dp
private val IndicatorHeight = 4.dp
private val ClearIconSize = 16.dp
private val TrailingGap = 8.dp
private val SubTextSpacing = 8.dp
private const val PROCESSING_SEGMENT_FRACTION = 0.3f
private const val DEFAULT_PLACEHOLDER = "텍스트를 입력해주세요"

/**
 * JD 링크 입력 필드. 상태에 따라 하단 indicator·클리어 버튼·서브텍스트가 달라진다.
 */
@Composable
fun HilitJDLinkField(
    value: String,
    onValueChange: (String) -> Unit,
    type: HilitJDLinkFieldType,
    modifier: Modifier = Modifier,
    placeholder: String = DEFAULT_PLACEHOLDER,
    subText: String = "",
    onClearClick: (() -> Unit)? = null,
) {
    val editable = type != HilitJDLinkFieldType.Processing
    val showClear =
        type == HilitJDLinkFieldType.Edit ||
            type == HilitJDLinkFieldType.Complete ||
            type == HilitJDLinkFieldType.Error
    val showProcessingLabel = type == HilitJDLinkFieldType.Processing
    val showSubText =
        subText.isNotEmpty() &&
            (
                type == HilitJDLinkFieldType.Ready ||
                    type == HilitJDLinkFieldType.Complete ||
                    type == HilitJDLinkFieldType.Error
                )

    val surfaceColor =
        if (type == HilitJDLinkFieldType.Processing) {
            HilitTheme.colors.gray100
        } else {
            HilitTheme.colors.hilitWhite
        }
    val borderColor = HilitTheme.colors.gray100
    val textColor = HilitTheme.colors.hilitBlack800
    val placeholderColor = HilitTheme.colors.gray500
    val textStyle = HilitTheme.typography.body4

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = surfaceColor,
                        shape = FieldShape,
                    ),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = FieldHorizontalPadding,
                                vertical = FieldVerticalPadding,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = editable,
                        singleLine = true,
                        textStyle = textStyle.copy(color = textColor),
                        cursorBrush = SolidColor(textColor),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (value.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = textStyle,
                                        color = placeholderColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )

                    if (showProcessingLabel) {
                        Spacer(modifier = Modifier.width(TrailingGap))
                        Text(
                            text = "분석 중",
                            style = HilitTheme.typography.body9,
                            color = HilitTheme.colors.gray400,
                        )
                    }

                    if (showClear) {
                        Spacer(modifier = Modifier.width(TrailingGap))
                        HilitIcon(
                            asset = HilitIconAsset.Delete,
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier =
                                Modifier
                                    .size(ClearIconSize)
                                    .clickable { onClearClick?.invoke() },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(IndicatorHeight))
            }

            // border를 전체 overlay로 그린 뒤, indicator로 하단 stroke를 덮는다.
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .border(
                            width = FieldBorderWidth,
                            color = borderColor,
                            shape = FieldShape,
                        ),
            )

            LinkFieldIndicator(
                type = type,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(IndicatorHeight),
            )
        }

        if (showSubText) {
            HilitSubText(
                text = subText,
                type = type.toSubTextType(),
                modifier = Modifier.padding(top = SubTextSpacing),
            )
        }
    }
}

@Composable
private fun LinkFieldIndicator(
    type: HilitJDLinkFieldType,
    modifier: Modifier = Modifier,
) {
    when (type) {
        HilitJDLinkFieldType.Ready -> {
            Spacer(modifier = modifier)
        }

        HilitJDLinkFieldType.Focus,
        HilitJDLinkFieldType.Edit,
        HilitJDLinkFieldType.Complete,
            -> {
            Box(
                modifier = modifier.background(HilitTheme.colors.hilitGreen500),
            )
        }

        HilitJDLinkFieldType.Error -> {
            Box(
                modifier = modifier.background(HilitTheme.colors.error500),
            )
        }

        HilitJDLinkFieldType.Processing -> {
            ProcessingIndicator(modifier = modifier)
        }
    }
}

@Composable
private fun ProcessingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "hilitJdLinkProcessing")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "hilitJdLinkProcessingProgress",
    )

    BoxWithConstraints(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(HilitTheme.colors.gray200),
        )
        val segmentWidth = maxWidth * PROCESSING_SEGMENT_FRACTION
        val maxOffset = maxWidth - segmentWidth
        Box(
            modifier =
                Modifier
                    .offset(x = maxOffset * progress)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .background(HilitTheme.colors.hilitGreen500),
        )
    }
}

private fun HilitJDLinkFieldType.toSubTextType(): HilitSubTextType =
    when (this) {
        HilitJDLinkFieldType.Ready -> HilitSubTextType.Default
        HilitJDLinkFieldType.Complete -> HilitSubTextType.Success
        HilitJDLinkFieldType.Error -> HilitSubTextType.Error
        else -> HilitSubTextType.Default
    }

@Preview(
    name = "HilitJDLinkField",
    showBackground = true,
    backgroundColor = 0xFF4A4B50,
    widthDp = 360,
    heightDp = 720,
)
@Composable
private fun HilitJDLinkFieldPreview() {
    HilitTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(HilitTheme.colors.gray700)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HilitJDLinkFieldType.entries.forEach { type ->
                HilitJDLinkField(
                    value =
                        when (type) {
                            HilitJDLinkFieldType.Edit,
                            HilitJDLinkFieldType.Processing,
                            HilitJDLinkFieldType.Complete,
                            HilitJDLinkFieldType.Error,
                                -> "https://company.com/jobs/123"

                            else -> ""
                        },
                    onValueChange = {},
                    type = type,
                    onClearClick = {},
                    subText = "서브 텍스트를 입력해주세요",
                )
            }
        }
    }
}

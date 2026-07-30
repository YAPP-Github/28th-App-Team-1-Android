package com.dminus14.designsystem.component.bubblefield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/** 말풍선 크기 타입. */
enum class BubbleFieldType {
    /** 최소 너비 149dp의 작은 말풍선 */
    Small,

    /** 최소 너비 274dp의 큰 말풍선. padding 14×12 */
    Big,
}

/** 꼬리가 붙는 변. */
enum class BubbleFieldTailEdge {
    /** 본문 위쪽에 꼬리 */
    Top,

    /** 본문 아래쪽에 꼬리 */
    Bottom,
}

/** 꼬리의 좌·우 위치. */
enum class BubbleFieldTailAlign {
    /** 본문 왼쪽(시작) 쪽에 꼬리 */
    Left,

    /** 본문 오른쪽(끝) 쪽에 꼬리 */
    Right,
}

/** 직각 삼각형의 수직 변 방향(왼쪽/오른쪽). */
enum class BubbleFieldTailShape {
    /** 수직 변이 왼쪽. 끝이 왼쪽을 가리킨다 */
    Left,

    /** 수직 변이 오른쪽. 끝이 오른쪽을 가리킨다 */
    Right,
}

private val SmallHorizontalPadding = 14.dp
private val SmallVerticalPadding = 12.dp
private val BigHorizontalPadding = 14.dp
private val BigVerticalPadding = 12.dp
private val SmallMinWidth = 149.dp
private val BigMinWidth = 274.dp
private val BubbleTailWidth = 12.dp
private val BubbleTailHeight = 8.dp
private val BubbleTailEdgePadding = 40.dp

/**
 * 말풍선 필드. 크기 타입과 꼬리의 상·하 위치, 좌·우 정렬, 삼각형 방향을 지정한다.
 *
 * Figma: BubbleField (`2000:7532`, Big body `2000:7343`)
 *
 * @param text 말풍선 본문 문구
 * @param type Small(최소 너비 149dp) / Big(최소 너비 274dp, padding 14×12)
 * @param tailEdge 꼬리가 붙는 변(Top/Bottom)
 * @param tailAlign 꼬리의 좌·우 위치
 * @param tailShape 직각 삼각형의 수직 변 방향(Left/Right)
 * @param modifier 외부 레이아웃 Modifier
 */
@Composable
fun BubbleField(
    text: String,
    type: BubbleFieldType,
    tailEdge: BubbleFieldTailEdge,
    tailAlign: BubbleFieldTailAlign,
    tailShape: BubbleFieldTailShape,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = HilitTheme.colors.hilitBlack800
    val columnAlignment =
        when (tailAlign) {
            BubbleFieldTailAlign.Left -> Alignment.Start
            BubbleFieldTailAlign.Right -> Alignment.End
        }
    val horizontalPadding =
        when (type) {
            BubbleFieldType.Small -> SmallHorizontalPadding
            BubbleFieldType.Big -> BigHorizontalPadding
        }
    val verticalPadding =
        when (type) {
            BubbleFieldType.Small -> SmallVerticalPadding
            BubbleFieldType.Big -> BigVerticalPadding
        }
    val sizeModifier =
        when (type) {
            BubbleFieldType.Small -> Modifier.widthIn(min = SmallMinWidth)
            BubbleFieldType.Big -> Modifier.widthIn(min = BigMinWidth)
        }
    val bodyWidthModifier =
        when (type) {
            BubbleFieldType.Small -> Modifier.widthIn(min = SmallMinWidth)
            BubbleFieldType.Big -> Modifier.fillMaxWidth()
        }

    Column(
        modifier = modifier.then(sizeModifier),
        horizontalAlignment = columnAlignment,
    ) {
        if (tailEdge == BubbleFieldTailEdge.Top) {
            BubbleTail(
                edge = tailEdge,
                align = tailAlign,
                shape = tailShape,
                color = backgroundColor,
            )
        }

        Box(
            modifier =
                bodyWidthModifier
                    .background(
                        color = backgroundColor,
                        shape = RectangleShape,
                    ).padding(
                        horizontal = horizontalPadding,
                        vertical = verticalPadding,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = HilitTheme.typography.body5,
                color = HilitTheme.colors.hilitWhite,
                textAlign = TextAlign.Center
            )
        }

        if (tailEdge == BubbleFieldTailEdge.Bottom) {
            BubbleTail(
                edge = tailEdge,
                align = tailAlign,
                shape = tailShape,
                color = backgroundColor,
            )
        }
    }
}

@Composable
private fun BubbleTail(
    edge: BubbleFieldTailEdge,
    align: BubbleFieldTailAlign,
    shape: BubbleFieldTailShape,
    color: Color,
) {
    val edgePaddingModifier =
        when (align) {
            BubbleFieldTailAlign.Left -> Modifier.padding(start = BubbleTailEdgePadding)
            BubbleFieldTailAlign.Right -> Modifier.padding(end = BubbleTailEdgePadding)
        }

    Box(
        modifier =
            edgePaddingModifier
                .size(width = BubbleTailWidth, height = BubbleTailHeight)
                .drawBehind {
                    drawPath(
                        path =
                            bubbleTailPath(
                                edge = edge,
                                shape = shape,
                                width = size.width,
                                height = size.height,
                            ),
                        color = color,
                    )
                },
    )
}

private fun bubbleTailPath(
    edge: BubbleFieldTailEdge,
    shape: BubbleFieldTailShape,
    width: Float,
    height: Float,
): Path =
    Path().apply {
        when (edge) {
            BubbleFieldTailEdge.Bottom -> {
                when (shape) {
                    BubbleFieldTailShape.Left -> {
                        moveTo(0f, 0f)
                        lineTo(width, 0f)
                        lineTo(0f, height)
                    }

                    BubbleFieldTailShape.Right -> {
                        moveTo(0f, 0f)
                        lineTo(width, 0f)
                        lineTo(width, height)
                    }
                }
            }

            BubbleFieldTailEdge.Top -> {
                when (shape) {
                    BubbleFieldTailShape.Left -> {
                        moveTo(0f, height)
                        lineTo(width, height)
                        lineTo(0f, 0f)
                    }

                    BubbleFieldTailShape.Right -> {
                        moveTo(0f, height)
                        lineTo(width, height)
                        lineTo(width, 0f)
                    }
                }
            }
        }
        close()
    }

@Preview(
    name = "BubbleField Small",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
)
@Composable
private fun BubbleFieldSmallPreview() {
    HilitTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BubbleFieldTailEdge.entries.forEach { edge ->
                BubbleFieldTailAlign.entries.forEach { align ->
                    BubbleFieldTailShape.entries.forEach { shape ->
                        BubbleField(
                            text = "텍스트를 입력해주세요",
                            type = BubbleFieldType.Small,
                            tailEdge = edge,
                            tailAlign = align,
                            tailShape = shape,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "BubbleField Big",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
)
@Composable
private fun BubbleFieldBigPreview() {
    HilitTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BubbleFieldTailEdge.entries.forEach { edge ->
                BubbleFieldTailAlign.entries.forEach { align ->
                    BubbleFieldTailShape.entries.forEach { shape ->
                        BubbleField(
                            text = "텍스트를 입력해주세요",
                            type = BubbleFieldType.Big,
                            tailEdge = edge,
                            tailAlign = align,
                            tailShape = shape,
                        )
                    }
                }
            }
        }
    }
}

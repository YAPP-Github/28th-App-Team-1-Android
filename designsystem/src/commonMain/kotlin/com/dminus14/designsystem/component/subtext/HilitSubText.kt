package com.dminus14.designsystem.component.subtext

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/** 서브텍스트의 아이콘·색상 타입. */
enum class HilitSubTextType {
    /** 정보성. gray 아이콘·텍스트 */
    Default,

    /** 성공. 초록 체크·텍스트 */
    Success,

    /** 오류. 빨간 경고·텍스트 */
    Error,
}

private val IconSize = 16.dp
private val IconTextGap = 6.dp

/**
 * 필드 하단 등에 쓰는 서브 텍스트. 타입에 따라 아이콘·색이 달라진다.
 *
 * Figma: text-sub status=default/success/error (`2044:1655`, `2044:1657`, `2044:1656`)
 *
 * @param text 표시할 문구
 * @param type 아이콘과 텍스트 색을 결정하는 타입
 * @param modifier 외부 레이아웃 Modifier
 */
@Composable
fun HilitSubText(
    text: String,
    type: HilitSubTextType,
    modifier: Modifier = Modifier,
) {
    val textColor =
        when (type) {
            HilitSubTextType.Default -> HilitTheme.colors.gray300
            HilitSubTextType.Success -> HilitTheme.colors.hilitGreen800
            HilitSubTextType.Error -> HilitTheme.colors.error500
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(IconTextGap),
    ) {
        SubTextIcon(type = type)
        Text(
            text = text,
            style = HilitTheme.typography.body6,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SubTextIcon(type: HilitSubTextType) {
    val colors = HilitTheme.colors
    when (type) {
        HilitSubTextType.Default -> {
            DefaultInfoIcon(
                color = colors.gray200,
                modifier = Modifier.size(IconSize),
            )
        }

        HilitSubTextType.Success -> {
            SuccessIcon(
                backgroundColor = colors.hilitGreen600,
                checkColor = colors.hilitWhite,
                modifier = Modifier.size(IconSize),
            )
        }

        HilitSubTextType.Error -> {
            ErrorIcon(
                backgroundColor = colors.error500,
                markColor = colors.hilitWhite,
                modifier = Modifier.size(IconSize),
            )
        }
    }
}

@Composable
private fun DefaultInfoIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * (2f / 16f)
        drawCircle(
            color = color,
            radius = (size.minDimension - strokeWidth) / 2f,
            style = Stroke(width = strokeWidth),
        )
        val barWidth = size.width * (2f / 16f)
        val barHeight = size.height * (7f / 16f)
        drawRect(
            color = color,
            topLeft = Offset(x = size.width * (7f / 16f), y = size.height * (6f / 16f)),
            size = Size(width = barWidth, height = barHeight),
        )
        drawRect(
            color = color,
            topLeft = Offset(x = size.width * (7f / 16f), y = size.height * (3f / 16f)),
            size = Size(width = barWidth, height = barWidth),
        )
    }
}

@Composable
private fun SuccessIcon(
    backgroundColor: Color,
    checkColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawCircle(color = backgroundColor)
        val strokeWidth = size.minDimension * 0.12f
        val start = Offset(x = size.width * 0.22f, y = size.height * 0.52f)
        val mid = Offset(x = size.width * 0.42f, y = size.height * 0.7f)
        val end = Offset(x = size.width * 0.78f, y = size.height * 0.32f)
        drawLine(
            color = checkColor,
            start = start,
            end = mid,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = checkColor,
            start = mid,
            end = end,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun ErrorIcon(
    backgroundColor: Color,
    markColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawCircle(color = backgroundColor)
        val barWidth = size.width * (2f / 16f)
        val barHeight = size.height * (7f / 16f)
        drawRect(
            color = markColor,
            topLeft = Offset(x = size.width * (7f / 16f), y = size.height * (3f / 16f)),
            size = Size(width = barWidth, height = barHeight),
        )
        drawRect(
            color = markColor,
            topLeft = Offset(x = size.width * (7f / 16f), y = size.height * (11f / 16f)),
            size = Size(width = barWidth, height = barWidth),
        )
    }
}

@Preview(
    name = "HilitSubText",
    showBackground = true,
    backgroundColor = 0xFF1A1B1F,
    widthDp = 360,
)
@Composable
private fun HilitSubTextPreview() {
    HilitTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HilitSubTextType.entries.forEach { type ->
                HilitSubText(
                    text = "서브 텍스트를 입력해주세요",
                    type = type,
                )
            }
        }
    }
}

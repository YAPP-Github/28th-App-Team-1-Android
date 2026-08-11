package com.dminus14.designsystem.component.subtext

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
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
 * @param maxLines 텍스트 최대 줄 수. 기본 1(한 줄, 초과 시 말줄임). 서버 메시지 등 길이가
 *   가변인 문구에는 [Int.MAX_VALUE]로 넘겨 wrap 을 허용한다.
 */
@Composable
fun HilitSubText(
    text: String,
    type: HilitSubTextType,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
) {
    val textColor =
        when (type) {
            HilitSubTextType.Default -> HilitTheme.colors.gray300
            HilitSubTextType.Success -> HilitTheme.colors.hilitGreen800
            HilitSubTextType.Error -> HilitTheme.colors.error500
        }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(IconTextGap),
    ) {
        SubTextIcon(type = type)
        Text(
            text = text,
            style = HilitTheme.typography.body6,
            color = textColor,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SubTextIcon(type: HilitSubTextType) {
    val colors = HilitTheme.colors
    val (asset, tint) =
        when (type) {
            HilitSubTextType.Default -> HilitIconAsset.Info to colors.gray200
            HilitSubTextType.Success -> HilitIconAsset.Success to colors.hilitGreen600
            HilitSubTextType.Error -> HilitIconAsset.FillWarning to Color.Unspecified
        }

    HilitIcon(
        asset = asset,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(IconSize),
    )
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

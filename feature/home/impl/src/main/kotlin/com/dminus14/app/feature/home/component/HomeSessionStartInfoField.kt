package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val InfoFieldShape = RoundedCornerShape(8.dp)
private val InfoFieldHorizontalPadding = 12.dp
private val InfoFieldVerticalPadding = 10.dp
private val InfoFieldIconTextGap = 6.dp

/**
 * 회색 배경의 안내 문구 행 (예: "이용권이 하나 차감됩니다").
 * `HilitModal` 내부 `HilitModalInfoField`와 같은 룩이지만 원본이 private이라 홈 feature 로컬로 재현.
 */
@Composable
internal fun HomeSessionStartInfoField(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = HilitTheme.colors.gray50, shape = InfoFieldShape)
                .padding(
                    horizontal = InfoFieldHorizontalPadding,
                    vertical = InfoFieldVerticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(InfoFieldIconTextGap),
    ) {
        HilitIcon(
            asset = HilitIconAsset.Info,
            contentDescription = null,
            tint = HilitTheme.colors.gray500,
            modifier = Modifier.size(HilitIconAsset.Info.defaultSize),
        )
        Text(
            text = text,
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.gray500,
        )
    }
}

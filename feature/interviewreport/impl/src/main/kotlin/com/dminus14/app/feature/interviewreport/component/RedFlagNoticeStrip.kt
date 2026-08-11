package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 심각 레드플래그 안내 줄. 최대 2줄까지 노출한다 (기획서 §2-4).
 *
 * 문구 자체는 mapper 에서 이미 노출 3종만 골라 넣는다. 이 컴포저블은 렌더링만 책임진다.
 */
@Composable
internal fun RedFlagNoticeStrip(
    notices: List<String>,
    modifier: Modifier = Modifier,
) {
    if (notices.isEmpty()) return
    val colors = HilitTheme.colors
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.error200)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        notices.forEach { line ->
            Text(
                text = line,
                style = HilitTheme.typography.body7,
                color = colors.error500,
            )
        }
    }
}

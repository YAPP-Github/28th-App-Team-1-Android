package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 해상도 낮음 카드 상단 안내 배너 (기획서 §2-3).
 *
 * 문구 판별과 톤은 mapper 가 담당한다. 여기서는 렌더링만 책임진다.
 */
@Composable
internal fun ResolutionNoticeBanner(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Text(
        text = text,
        style = HilitTheme.typography.body7,
        color = colors.gray300,
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.gray900)
                .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}

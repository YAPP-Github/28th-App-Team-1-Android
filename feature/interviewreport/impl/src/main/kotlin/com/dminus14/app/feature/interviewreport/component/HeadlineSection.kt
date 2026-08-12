package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dminus14.app.feature.interviewreport.model.HeadlineUiModel
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 리포트 상단 한 줄 요약 타이틀 (Figma Node: 443:6893).
 *
 * 다크 시안에서는 별도 배경 배너 없이 흰색 굵은 제목으로 렌더한다. 톤(POSITIVE/NEUTRAL)은
 * 분석부족 상태가 별도 Phase 로 분기되므로 색을 달리하지 않고 동일한 제목 스타일을 사용한다.
 */
@Composable
internal fun HeadlineSection(
    headline: HeadlineUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Text(
        text = headline.text,
        style = HilitTheme.typography.head3,
        color = colors.hilitWhite,
        modifier = modifier.fillMaxWidth(),
    )
}

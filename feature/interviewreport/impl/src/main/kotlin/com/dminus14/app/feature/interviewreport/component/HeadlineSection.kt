package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.HeadlineTone
import com.dminus14.app.feature.interviewreport.model.HeadlineUiModel
import com.dminus14.designsystem.theme.HilitTheme

/** 리포트 상단 한 줄 요약. 톤에 따라 배경·글자색이 달라진다 (기획서 §2-2). */
@Composable
internal fun HeadlineSection(
    headline: HeadlineUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    val (background, textColor) =
        when (headline.tone) {
            HeadlineTone.POSITIVE -> colors.hilitGreen200 to colors.hilitGreen800
            HeadlineTone.NEUTRAL -> colors.gray100 to colors.gray900
            HeadlineTone.INSUFFICIENT -> colors.gray50 to colors.gray700
        }
    Text(
        text = headline.text,
        style = HilitTheme.typography.sub3,
        color = textColor,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .padding(horizontal = 16.dp, vertical = 20.dp),
    )
}

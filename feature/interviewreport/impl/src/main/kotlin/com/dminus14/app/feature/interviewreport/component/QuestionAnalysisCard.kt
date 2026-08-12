package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * "Hilit의 질문 분석" 메시지 카드 (Figma Node: 443:6923).
 *
 * 질문 의도 제목([title], questionIntentTitle)과 질문 분석 본문([intent], questionIntent)을
 * AI 스파클 아이콘과 함께 렌더한다. 둘 다 비어 있으면 렌더하지 않는다.
 */
@Composable
internal fun QuestionAnalysisCard(
    title: String?,
    intent: String?,
    modifier: Modifier = Modifier,
) {
    val hasTitle = !title.isNullOrBlank()
    val hasIntent = !intent.isNullOrBlank()
    if (!hasTitle && !hasIntent) return
    val colors = HilitTheme.colors
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.hilitBlack800)
                .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HilitIcon(
                asset = HilitIconAsset.AiSparkle,
                contentDescription = null,
                tint = colors.hilitGreen500,
                modifier = Modifier.size(36.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Hilit의 질문 분석",
                    style = HilitTheme.typography.body9,
                    color = colors.gray400,
                )
                if (hasTitle) {
                    Text(
                        text = title.orEmpty(),
                        style = HilitTheme.typography.body1,
                        color = colors.hilitWhite,
                    )
                }
            }
        }
        if (hasIntent) {
            Text(
                text = intent.orEmpty(),
                style = HilitTheme.typography.body7,
                color = colors.gray200,
            )
        }
    }
}

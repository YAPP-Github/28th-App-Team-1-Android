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
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 리포트 카드 하나. 상단부터
 * - 카드 라벨 (질문 1-1)
 * - 질문 텍스트 + 질문 분석
 * - 해상도 낮음 안내 (있을 때)
 * - 답변 대본 (하이라이트 강조)
 * - 카드 레벨 레드플래그 안내 (있을 때)
 * 순으로 렌더한다.
 */
@Composable
internal fun ReportCard(
    card: CardUiModel,
    cardIndex: Int,
    onHighlightClick: (cardIndex: Int, spanIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.hilitWhite)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = card.label,
            style = HilitTheme.typography.body8,
            color = colors.hilitGreen800,
        )
        Text(
            text = card.questionText,
            style = HilitTheme.typography.sub7,
            color = colors.gray900,
        )
        card.questionIntent?.takeIf { it.isNotBlank() }?.let { intent ->
            Text(
                text = intent,
                style = HilitTheme.typography.body7,
                color = colors.gray700,
            )
        }
        card.resolutionNotice?.let { notice ->
            ResolutionNoticeBanner(text = notice)
        }
        if (card.highlights.isEmpty()) {
            PlainTranscript(text = card.transcript)
        } else {
            TranscriptWithHighlights(
                card = card,
                onHighlightClick = { spanIndex -> onHighlightClick(cardIndex, spanIndex) },
            )
        }
        if (card.cardRedFlagNotices.isNotEmpty()) {
            RedFlagNoticeStrip(notices = card.cardRedFlagNotices)
        }
    }
}

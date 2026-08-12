package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 상세 리포트에서 선택된 질문 카드 하나 (Figma Node: 443:6917).
 *
 * 다크 시안 기준으로 상단부터
 * - Q 배지 + 질문 텍스트
 * - Hilit 질문 분석 카드 ([QuestionAnalysisCard])
 * - 해상도 낮음 안내 (있을 때)
 * - 답변 대본 (하이라이트 강조)
 * 순으로 렌더한다. 별도 카드 배경 없이 화면 다크 배경 위에 그린다.
 *
 * 카드 레드플래그(`cardRedFlagNotices`)는 카드 안에 별도 텍스트 줄로 노출하지 않는다.
 * Figma 원본(443:6917)에도 해당 UI가 없고, 레드플래그 안내는 "상세 리포트" 타이틀 옆
 * [DetailReportSectionHeader] 아이콘/말풍선으로만 노출한다.
 */
@Composable
internal fun ReportCard(
    card: CardUiModel,
    cardIndex: Int,
    onHighlightClick: (cardIndex: Int, spanIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            QuestionRow(questionText = card.questionText)
            QuestionAnalysisCard(
                title = card.questionIntentTitle,
                intent = card.questionIntent,
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
    }
}

@Composable
private fun QuestionRow(questionText: String) {
    val colors = HilitTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .size(20.dp)
                    .background(colors.gray900),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Q",
                style = HilitTheme.typography.body5,
                color = colors.gray200,
            )
        }
        Text(
            text = questionText,
            style = HilitTheme.typography.body3,
            color = colors.hilitWhite,
            modifier = Modifier.weight(1f),
        )
    }
}

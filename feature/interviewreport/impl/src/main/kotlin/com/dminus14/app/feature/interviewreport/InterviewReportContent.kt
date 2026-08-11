@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.component.HeadlineSection
import com.dminus14.app.feature.interviewreport.component.HighlightDetailBottomSheet
import com.dminus14.app.feature.interviewreport.component.RedFlagNoticeStrip
import com.dminus14.app.feature.interviewreport.component.ReportCard
import com.dminus14.app.feature.interviewreport.component.VideoRewatchButton
import com.dminus14.app.feature.interviewreport.model.ReportUiModel
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 리포트 화면 순수 UI. State 만 받아 렌더하고 콜백으로 이벤트를 전달한다.
 */
@Composable
internal fun InterviewReportContent(
    state: InterviewReportState,
    onIntent: (InterviewReportIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Box(
        modifier = modifier.fillMaxSize().background(colors.gray50),
    ) {
        when (val phase = state.phase) {
            InterviewReportState.Phase.Loading ->
                CenteredMessage(content = { HilitLoadingIndicator() })

            InterviewReportState.Phase.Failed ->
                CenteredMessage(
                    content = {
                        Text(
                            text = "리포트를 불러오지 못했어요. 다시 시도해 주세요.",
                            style = HilitTheme.typography.sub7,
                            color = colors.gray900,
                        )
                    },
                )

            InterviewReportState.Phase.InsufficientAnalysis ->
                CenteredMessage(
                    content = {
                        Text(
                            text = "이번 면접 답변이 분석하기에 부족했어요. 다음 연습에서 조금 더 자세히 답해보세요.",
                            style = HilitTheme.typography.sub7,
                            color = colors.gray900,
                        )
                    },
                )

            is InterviewReportState.Phase.Ready ->
                ReadyReport(
                    report = phase.report,
                    onIntent = onIntent,
                )
        }

        val selected = state.selectedHighlight
        val phase = state.phase
        if (selected != null && phase is InterviewReportState.Phase.Ready) {
            val card = phase.report.cards.getOrNull(selected.cardIndex)
            val highlight = card?.highlights?.getOrNull(selected.spanIndex)
            if (card != null && highlight != null) {
                HighlightDetailBottomSheet(
                    highlight = highlight,
                    transcript = card.transcript,
                    showWatchSceneButton = true,
                    onDismiss = { onIntent(InterviewReportIntent.DismissHighlight) },
                    onWatchScene = {
                        highlight.startSec?.let { onIntent(InterviewReportIntent.ClickWatchScene(it)) }
                    },
                )
            }
        }
    }
}

@Composable
private fun ReadyReport(
    report: ReportUiModel,
    onIntent: (InterviewReportIntent) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeadlineSection(headline = report.headline)
        RedFlagNoticeStrip(notices = report.redFlagNotices)
        VideoRewatchButton(
            video = report.video,
            onClick = { onIntent(InterviewReportIntent.ClickWatchVideo) },
        )
        report.cards.forEachIndexed { index, card ->
            ReportCard(
                card = card,
                cardIndex = index,
                onHighlightClick = { cardIndex, spanIndex ->
                    onIntent(InterviewReportIntent.ClickHighlight(cardIndex, spanIndex))
                },
            )
        }
    }
}

@Composable
private fun CenteredMessage(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

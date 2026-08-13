package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.HighlightUiModel
import com.dminus14.app.feature.interviewreport.model.HighlightUiReason
import com.dminus14.app.feature.interviewreport.model.HighlightUiTone
import com.dminus14.designsystem.component.bottomsheet.HilitBottomSheet
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitColors
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 하이라이트 상세 시트 (Figma Node: 443:7392). 다크 시트로 렌더한다.
 *
 * - 분석 내용 헤더 + "영상 보러가기" 버튼(영상 위에서 열렸으면 숨김)
 * - 하이라이트 발췌 (톤 색)
 * - "Hilit의 답변 분석" 카드 (제목 · 분석)
 * - 카드 레드플래그 안내 (있을 때)
 * - reason 별 "다음 대비". OFF_INTENT 는 질문 의도 ↔ 내 답변 대비 블록,
 *   그 외 reason 은 코칭 한 줄 미니 카드. 유의미한 콘텐츠가 없으면 생략한다.
 *
 * @param highlight 노출할 하이라이트
 * @param transcript 원본 대본. 하이라이트 구간을 발췌해서 상단에 보여준다
 * @param cardRedFlagNotices 하이라이트가 속한 카드의 레드플래그 안내
 * @param showWatchSceneButton 영상 위에서 열린 경우엔 false 로 넣어 [영상 보러가기] 를 숨긴다
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList")
internal fun HighlightDetailBottomSheet(
    highlight: HighlightUiModel,
    transcript: String,
    cardRedFlagNotices: List<String>,
    showWatchSceneButton: Boolean,
    onDismiss: () -> Unit,
    onWatchScene: () -> Unit,
) {
    val colors = HilitTheme.colors
    HilitBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.hilitBlack900,
        content = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                DiagnosisBlock(
                    highlight = highlight,
                    transcriptSlice =
                        transcript.safeSlice(highlight.startIndex, highlight.endIndex),
                    showWatchSceneButton = showWatchSceneButton && highlight.startSec != null,
                    onWatchScene = onWatchScene,
                )
                if (cardRedFlagNotices.isNotEmpty()) {
                    RedFlagNoticeStrip(notices = cardRedFlagNotices)
                }
                NextPreparationBlock(highlight = highlight)
            }
        },
    )
}

@Composable
private fun DiagnosisBlock(
    highlight: HighlightUiModel,
    transcriptSlice: String,
    showWatchSceneButton: Boolean,
    onWatchScene: () -> Unit,
) {
    val colors = HilitTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "분석 내용",
                style = HilitTheme.typography.sub7,
                color = colors.hilitWhite,
            )
            if (showWatchSceneButton) {
                WatchSceneButton(onClick = onWatchScene)
            }
        }
        if (transcriptSlice.isNotBlank()) {
            Text(
                text = transcriptSlice,
                style = HilitTheme.typography.body3,
                color = highlight.tone.toColor(colors),
            )
        }
        AnalysisMessageCard(highlight = highlight)
    }
}

@Composable
private fun WatchSceneButton(onClick: () -> Unit) {
    val colors = HilitTheme.colors
    Row(
        modifier =
            Modifier
                .background(colors.gray900)
                .clickable(onClick = onClick)
                .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = HilitIconAsset.Video,
            contentDescription = null,
            tint = colors.hilitWhite,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "영상 보러가기",
            style = HilitTheme.typography.body5,
            color = colors.hilitWhite,
        )
    }
}

@Composable
private fun AnalysisMessageCard(highlight: HighlightUiModel) {
    val colors = HilitTheme.colors
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(colors.hilitBlack800)
                .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HilitIcon(
                asset = highlight.tone.toAnalyzeIconAsset(),
                contentDescription = null,
                tint = highlight.tone.toColor(colors),
                modifier = Modifier.size(36.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Hilit의 답변 분석",
                    style = HilitTheme.typography.body9,
                    color = colors.gray400,
                )
                Text(
                    text = highlight.title,
                    style = HilitTheme.typography.body1,
                    color = colors.hilitWhite,
                )
            }
        }
        if (highlight.analysis.isNotBlank()) {
            Text(
                text = highlight.analysis,
                style = HilitTheme.typography.body7,
                color = colors.gray200,
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun NextPreparationBlock(highlight: HighlightUiModel) {
    val colors = HilitTheme.colors
    when (highlight.reason) {
        HighlightUiReason.OFF_INTENT -> {
            val intent = highlight.questionIntent
            val intentTitle = highlight.questionIntentTitle
            val topic = highlight.answerTopicTitle
            if (intent.isNullOrBlank() && intentTitle.isNullOrBlank() &&
                topic.isNullOrBlank()
            ) {
                return
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "질문의 의도를 다시 확인해 보세요",
                    style = HilitTheme.typography.sub7,
                    color = colors.hilitWhite,
                )
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ReasonTag(text = "질문 의도", green = false)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            intentTitle?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    style = HilitTheme.typography.body3,
                                    color = colors.gray50,
                                )
                            }
                            intent?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    style = HilitTheme.typography.body7,
                                    color = colors.gray200,
                                )
                            }
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(colors.gray800))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ReasonTag(text = "내 답변", green = true)
                        Text(
                            text = topic.orEmpty(),
                            style = HilitTheme.typography.body3,
                            color = colors.gray50,
                        )
                    }
                }
                MiniHintCard(text = "다음에는 질문에서 무엇을 묻고 있는지 짚어보세요!")
            }
        }

        HighlightUiReason.PROBE_WORTHY -> {
            if (highlight.followUpQuestions.isEmpty()) return
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "이런 꼬리질문이 이어질 수 있어요",
                    style = HilitTheme.typography.sub7,
                    color = colors.hilitWhite,
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    highlight.followUpQuestions.forEach { question ->
                        MiniHintCard(text = question)
                    }
                }
            }
        }

        HighlightUiReason.SHALLOW -> {
            MiniHintCard(text = "다음엔 조금 더 자세히 답해보세요.")
        }

        HighlightUiReason.SUFFICIENT -> {
            MiniHintCard(text = "여기는 면접관이 더 캐물을 게 없을 만큼 충분히 답하셨어요.")
        }

        HighlightUiReason.UNKNOWN -> {
            Unit
        }
    }
}

@Composable
private fun ReasonTag(
    text: String,
    green: Boolean,
) {
    val colors = HilitTheme.colors
    Text(
        text = text,
        style = HilitTheme.typography.body5,
        color = if (green) colors.hilitGreen500 else colors.gray200,
        modifier =
            Modifier
                .background(colors.gray900)
                .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

@Composable
private fun MiniHintCard(text: String) {
    val colors = HilitTheme.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(colors.gray800)
                .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = HilitIconAsset.AiSparkle,
            contentDescription = null,
            tint = colors.hilitGreen500,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = HilitTheme.typography.body7,
            color = colors.hilitWhite,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun HighlightUiTone.toColor(colors: HilitColors): Color =
    when (this) {
        HighlightUiTone.POSITIVE -> colors.positive500
        HighlightUiTone.NEGATIVE -> colors.error400
        HighlightUiTone.NEUTRAL -> colors.gray50
    }

/**
 * "Hilit의 답변 분석" 카드 아이콘 (Figma: hilit analyze/36px/success, hilit analyze/36px/problem).
 * 톤이 없는 NEUTRAL(서버 UNKNOWN fallback)은 시안에 정의된 배지가 없어 기존 AiSparkle로 대체한다.
 */
private fun HighlightUiTone.toAnalyzeIconAsset(): HilitIconAsset =
    when (this) {
        HighlightUiTone.POSITIVE -> HilitIconAsset.AnalyzeSuccess
        HighlightUiTone.NEGATIVE -> HilitIconAsset.AnalyzeProblem
        HighlightUiTone.NEUTRAL -> HilitIconAsset.AiSparkle
    }

/** [TranscriptWithHighlights] 도 같은 클램프로 인덱스 밖 크래시를 막기 위해 재사용한다. */
internal fun String.safeSlice(
    start: Int,
    end: Int,
): String {
    if (isEmpty()) return ""
    val safeStart = start.coerceIn(0, length)
    val safeEnd = end.coerceIn(safeStart, length)
    return substring(safeStart, safeEnd)
}

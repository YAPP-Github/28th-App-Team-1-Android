package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.HighlightUiModel
import com.dminus14.app.feature.interviewreport.model.HighlightUiReason
import com.dminus14.designsystem.component.bottomsheet.HilitBottomSheet
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 하이라이트 상세 시트. 진입 depth 는 두 개다 (기획서 §2-5).
 *
 * - Depth 1 : 하이라이트 문장 · 행동형 키워드 태그 · 분석
 * - Depth 2 : reason 별 "다음 대비" 콘텐츠. 유의미한 콘텐츠가 없으면 depth 2 자체를 생략한다
 *
 * @param highlight 노출할 하이라이트
 * @param transcript 원본 대본. 하이라이트 구간을 다시 발췌해서 상단에 보여준다
 * @param showWatchSceneButton 영상 위에서 열린 경우엔 false 로 넣어 [이 장면 영상으로 보기] 를 숨긴다
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HighlightDetailBottomSheet(
    highlight: HighlightUiModel,
    transcript: String,
    showWatchSceneButton: Boolean,
    onDismiss: () -> Unit,
    onWatchScene: () -> Unit,
) {
    HilitBottomSheet(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DiagnosisBlock(
                    highlight = highlight,
                    transcriptSlice =
                        transcript.safeSlice(
                            highlight.startIndex,
                            highlight.endIndex,
                        ),
                )
                if (showWatchSceneButton && highlight.startSec != null) {
                    WatchSceneButton(onClick = onWatchScene)
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
) {
    val colors = HilitTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "\"$transcriptSlice\"",
            style = HilitTheme.typography.body3,
            color = colors.gray800,
        )
        Text(
            text = "[${highlight.title}]",
            style = HilitTheme.typography.body5,
            color = colors.hilitGreen800,
        )
        if (highlight.analysis.isNotBlank()) {
            Text(
                text = highlight.analysis,
                style = HilitTheme.typography.body4,
                color = colors.gray700,
            )
        }
    }
}

@Composable
private fun WatchSceneButton(onClick: () -> Unit) {
    val colors = HilitTheme.colors
    Text(
        text = "이 장면 영상으로 보기",
        style = HilitTheme.typography.sub7,
        color = colors.hilitGreen800,
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.hilitGreen200)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Composable
private fun NextPreparationBlock(highlight: HighlightUiModel) {
    val colors = HilitTheme.colors
    when (highlight.reason) {
        HighlightUiReason.PROBE_WORTHY -> {
            if (highlight.followUpQuestions.isEmpty()) return
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "다음 대비",
                    style = HilitTheme.typography.sub7,
                    color = colors.gray900,
                )
                highlight.followUpQuestions.forEach { question ->
                    Text(
                        text = "• $question",
                        style = HilitTheme.typography.body4,
                        color = colors.gray800,
                    )
                }
            }
        }

        HighlightUiReason.OFF_INTENT -> {
            val intent = highlight.questionIntent
            val topic = highlight.answerTopicTitle
            if (intent.isNullOrBlank() && topic.isNullOrBlank()) return
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "다음 대비",
                    style = HilitTheme.typography.sub7,
                    color = colors.gray900,
                )
                intent?.let {
                    Text(
                        text = "질문이 물은 것: $it",
                        style = HilitTheme.typography.body4,
                        color = colors.gray800,
                    )
                }
                topic?.let {
                    Text(
                        text = "내 답변이 닿은 곳: $it",
                        style = HilitTheme.typography.body4,
                        color = colors.gray800,
                    )
                }
                Text(
                    text = "다음엔 질문이 묻는 것부터 짚고 시작해보세요.",
                    style = HilitTheme.typography.body7,
                    color = colors.gray600,
                )
            }
        }

        HighlightUiReason.SHALLOW -> {
            Text(
                text = "다음엔 조금 더 자세히 답해보세요.",
                style = HilitTheme.typography.body7,
                color = colors.gray700,
            )
        }

        HighlightUiReason.SUFFICIENT -> {
            Text(
                text = "여기는 면접관이 더 캐물 게 없을 만큼 충분히 답하셨어요.",
                style = HilitTheme.typography.body7,
                color = colors.gray700,
            )
        }

        HighlightUiReason.UNKNOWN -> {
            Unit
        }
    }
}

private fun String.safeSlice(
    start: Int,
    end: Int,
): String {
    if (isEmpty()) return ""
    val safeStart = start.coerceIn(0, length)
    val safeEnd = end.coerceIn(safeStart, length)
    return substring(safeStart, safeEnd)
}

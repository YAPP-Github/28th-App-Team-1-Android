@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.PreviewInterviewReport
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 리포트 특화 컴포넌트 Preview 모음. 다크 배경 위에서 합성 데이터로 렌더한다
 * (실제 사용자 데이터 미사용, CONSTITUTION 사용자 데이터 조항).
 */
@Composable
private fun DarkPreview(content: @Composable () -> Unit) {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitBlack900)
                    .padding(20.dp),
        ) {
            content()
        }
    }
}

@Preview(name = "HeadlineSection")
@Composable
private fun HeadlineSectionPreview() {
    DarkPreview {
        HeadlineSection(headline = PreviewInterviewReport.readyReport.headline)
    }
}

@Preview(name = "VideoCountdownCard - 진행")
@Composable
private fun VideoCountdownCardPreview() {
    DarkPreview {
        VideoCountdownCard(
            video = PreviewInterviewReport.sampleVideo,
            expirySeconds = 86_373L,
            onClick = {},
        )
    }
}

@Preview(name = "VideoCountdownCard - 만료")
@Composable
private fun VideoCountdownCardExpiredPreview() {
    DarkPreview {
        VideoCountdownCard(
            video = PreviewInterviewReport.expiredReport.video,
            expirySeconds = null,
            onClick = {},
        )
    }
}

@Preview(name = "DetailReportSectionHeader - 해상도 낮음 안내 없음")
@Composable
private fun DetailReportSectionHeaderPreview() {
    DarkPreview {
        DetailReportSectionHeader(resolutionNotice = null)
    }
}

@Preview(name = "DetailReportSectionHeader - 해상도 낮음 안내 (진입 시 말풍선 자동 노출)")
@Composable
private fun DetailReportSectionHeaderResolutionNoticePreview() {
    DarkPreview {
        DetailReportSectionHeader(
            resolutionNotice = PreviewInterviewReport.sampleResolutionNotice,
        )
    }
}

@Preview(name = "QuestionTabRow")
@Composable
private fun QuestionTabRowPreview() {
    DarkPreview {
        QuestionTabRow(
            cards =
                listOf(
                    PreviewInterviewReport.sampleCard,
                    PreviewInterviewReport.sampleRedFlagCard,
                ),
            selectedIndex = 0,
            onSelect = {},
        )
    }
}

@Preview(name = "QuestionAnalysisCard")
@Composable
private fun QuestionAnalysisCardPreview() {
    DarkPreview {
        val card = PreviewInterviewReport.sampleCard
        QuestionAnalysisCard(title = card.questionIntentTitle, intent = card.questionIntent)
    }
}

@Preview(name = "ReportCard", heightDp = 520)
@Composable
private fun ReportCardPreview() {
    DarkPreview {
        ReportCard(
            card = PreviewInterviewReport.sampleCard,
            cardIndex = 0,
            onHighlightClick = { _, _ -> },
        )
    }
}

@Preview(name = "ReportCard - 해상도 낮음 안내", heightDp = 560)
@Composable
private fun ReportCardResolutionNoticePreview() {
    DarkPreview {
        ReportCard(
            card = PreviewInterviewReport.sampleResolutionNoticeCard,
            cardIndex = 0,
            onHighlightClick = { _, _ -> },
        )
    }
}

@Preview(name = "FeedbackRequestCard")
@Composable
private fun FeedbackRequestCardPreview() {
    DarkPreview {
        FeedbackRequestCard(model = PreviewInterviewReport.sampleGuestFeedback, onClick = {})
    }
}

@Preview(name = "GuestFeedbackSection - 1명 참여 (초대 카드 노출)", heightDp = 520)
@Composable
private fun GuestFeedbackSectionOneGuestPreview() {
    DarkPreview {
        GuestFeedbackSection(
            model = PreviewInterviewReport.sampleGuestFeedbackWithOneGuest,
            selectedGuestIndex = 0,
            onSelectGuest = {},
            onClickInvite = {},
        )
    }
}

@Preview(name = "GuestFeedbackSection - 정원 4명 참여 (초대 카드 숨김)", heightDp = 520)
@Composable
private fun GuestFeedbackSectionFullPreview() {
    DarkPreview {
        GuestFeedbackSection(
            model = PreviewInterviewReport.sampleGuestFeedbackFull,
            selectedGuestIndex = 0,
            onSelectGuest = {},
            onClickInvite = {},
        )
    }
}

@Preview(name = "RedFlagNoticeStrip")
@Composable
private fun RedFlagNoticeStripPreview() {
    DarkPreview {
        RedFlagNoticeStrip(notices = listOf("답변 사이에 사실관계가 엇갈린 지점이 있었어요."))
    }
}

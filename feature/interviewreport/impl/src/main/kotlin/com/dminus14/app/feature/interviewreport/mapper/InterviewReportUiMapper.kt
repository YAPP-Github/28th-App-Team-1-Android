package com.dminus14.app.feature.interviewreport.mapper

import com.dminus14.app.domain.model.GuestFeedbackSummary
import com.dminus14.app.domain.model.HighlightReason
import com.dminus14.app.domain.model.HighlightSpan
import com.dminus14.app.domain.model.HighlightTone
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportCard
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.model.InterviewReportVideo
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.app.feature.interviewreport.model.GuestFeedbackUiModel
import com.dminus14.app.feature.interviewreport.model.HeadlineTone
import com.dminus14.app.feature.interviewreport.model.HeadlineUiModel
import com.dminus14.app.feature.interviewreport.model.HighlightUiModel
import com.dminus14.app.feature.interviewreport.model.HighlightUiReason
import com.dminus14.app.feature.interviewreport.model.HighlightUiTone
import com.dminus14.app.feature.interviewreport.model.ReportUiModel
import com.dminus14.app.feature.interviewreport.model.VideoUiModel

/**
 * 도메인 [InterviewReport] 를 화면이 그대로 렌더할 수 있는 [ReportUiModel] 로 변환한다.
 *
 * - 분석 부족 상태 / 심각 레드플래그가 있을 때는 헤드라인이 톤을 낮춘다 (기획서 §2-2).
 * - 하이라이트 span 은 대본 길이를 넘어서지 않도록 clamp 하여 UI 크래시를 예방한다.
 * - 카드는 axisOrder → depthLevel 오름차순 으로 정렬한다.
 */
object InterviewReportUiMapper {
    private const val DEFAULT_INSUFFICIENT_HEADLINE =
        "이번 면접의 답변이 충분하지 않아요. 다음 면접 연습 때는 조금 더 충분한 답변을 말씀해주세요."

    fun map(report: InterviewReport): ReportUiModel {
        val cards =
            report.cards
                .orEmpty()
                .sortedWith(compareBy({ it.axisOrder }, { it.depthLevel }))
                .map { it.toUi() }
        val hasSevereRedFlag = cards.any { it.cardRedFlagNotices.isNotEmpty() }
        val redFlagNotices =
            cards.flatMap { it.cardRedFlagNotices }.distinct().take(
                MAX_RED_FLAG_LINES,
            )

        return ReportUiModel(
            headline = report.toHeadline(hasSevereRedFlag = hasSevereRedFlag),
            redFlagNotices = redFlagNotices,
            video = report.video?.toUi(),
            cards = cards,
            guestFeedback = report.guestFeedback?.toUi(),
        )
    }

    private fun GuestFeedbackSummary.toUi(): GuestFeedbackUiModel =
        GuestFeedbackUiModel(
            participantCount = participantCount,
            capacity = GUEST_FEEDBACK_CAPACITY,
            title = "지인에게 면접 영상 보내기",
            subtitle = "${GUEST_FEEDBACK_CAPACITY}명에게 영상을 공유하고 태도 분석을 받아보세요!",
        )

    private fun InterviewReport.toHeadline(hasSevereRedFlag: Boolean): HeadlineUiModel {
        val insufficient = status == InterviewReportStatus.INSUFFICIENT_ANALYSIS
        return when {
            insufficient -> {
                HeadlineUiModel(
                    text = headline ?: DEFAULT_INSUFFICIENT_HEADLINE,
                    tone = HeadlineTone.INSUFFICIENT,
                )
            }

            hasSevereRedFlag -> {
                HeadlineUiModel(
                    text = headline.orEmpty(),
                    tone = HeadlineTone.NEUTRAL,
                )
            }

            else -> {
                HeadlineUiModel(
                    text = headline.orEmpty(),
                    tone = HeadlineTone.POSITIVE,
                )
            }
        }
    }

    private fun InterviewReportVideo.toUi(): VideoUiModel {
        val expiredNotice =
            if (expired) {
                "24시간이 지나서 영상이 사라졌어요. 다음 면접 연습 때는 지인 피드백을 받아보세요. 더 오랫동안 영상을 볼 수 있어요."
            } else {
                null
            }
        return VideoUiModel(url = url, expired = expired, expiredNotice = expiredNotice)
    }

    private fun InterviewReportCard.toUi(): CardUiModel {
        val transcriptLength = transcript.length
        return CardUiModel(
            label = "질문 $axisOrder-$depthLevel",
            questionText = questionText,
            questionIntentTitle = questionIntentTitle,
            questionIntent = questionIntent,
            transcript = transcript,
            highlights = highlightSpans.map { it.toUi(transcriptLength) },
            resolutionNotice = resolutionNotice,
            cardRedFlagNotices = cardRedFlagNotices.orEmpty(),
        )
    }

    private fun HighlightSpan.toUi(transcriptLength: Int): HighlightUiModel {
        val safeStart = startIndex.coerceIn(0, transcriptLength)
        val safeEnd = endIndex.coerceIn(safeStart, transcriptLength)
        return HighlightUiModel(
            startIndex = safeStart,
            endIndex = safeEnd,
            tone = tone.toUi(),
            reason = reason.toUi(),
            title = title,
            analysis = analysis,
            followUpQuestions = followUpQuestions,
            startSec = startSec,
            answerTopicTitle = answerTopicTitle,
            questionIntentTitle = questionIntentTitle,
            questionIntent = questionIntent,
        )
    }

    private fun HighlightTone.toUi(): HighlightUiTone =
        when (this) {
            HighlightTone.GOOD -> HighlightUiTone.POSITIVE
            HighlightTone.IMPROVE -> HighlightUiTone.NEGATIVE
            HighlightTone.UNKNOWN -> HighlightUiTone.NEUTRAL
        }

    private fun HighlightReason.toUi(): HighlightUiReason =
        when (this) {
            HighlightReason.PROBE_WORTHY -> HighlightUiReason.PROBE_WORTHY
            HighlightReason.OFF_INTENT -> HighlightUiReason.OFF_INTENT
            HighlightReason.SHALLOW -> HighlightUiReason.SHALLOW
            HighlightReason.SUFFICIENT -> HighlightUiReason.SUFFICIENT
            HighlightReason.UNKNOWN -> HighlightUiReason.UNKNOWN
        }

    private const val MAX_RED_FLAG_LINES: Int = 2

    /** 지인 피드백 정원(면접당 최대 4명, feedback.md). */
    private const val GUEST_FEEDBACK_CAPACITY: Int = 4
}

package com.dminus14.app.feature.interviewreport.mapper

import com.dminus14.app.domain.model.HighlightReason
import com.dminus14.app.domain.model.HighlightSpan
import com.dminus14.app.domain.model.HighlightTone
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportCard
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.model.InterviewReportVideo
import com.dminus14.app.feature.interviewreport.model.HeadlineTone
import com.dminus14.app.feature.interviewreport.model.HighlightUiReason
import com.dminus14.app.feature.interviewreport.model.HighlightUiTone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewReportUiMapperTest {
    @Test
    fun `정상 READY 상태는 헤드라인 톤을 POSITIVE 로 매핑한다`() {
        val report = sampleReport(status = InterviewReportStatus.READY, headline = "잘 답하셨어요.")

        val ui = InterviewReportUiMapper.map(report)

        assertEquals(HeadlineTone.POSITIVE, ui.headline.tone)
        assertEquals("잘 답하셨어요.", ui.headline.text)
        assertTrue(ui.redFlagNotices.isEmpty())
    }

    @Test
    fun `분석 부족 상태는 헤드라인 톤을 INSUFFICIENT 로 매핑하고 기본 문구를 채워준다`() {
        val report = sampleReport(status = InterviewReportStatus.INSUFFICIENT_ANALYSIS, headline = null)

        val ui = InterviewReportUiMapper.map(report)

        assertEquals(HeadlineTone.INSUFFICIENT, ui.headline.tone)
        assertTrue(ui.headline.text.isNotBlank())
    }

    @Test
    fun `카드에 레드플래그 안내가 있으면 헤드라인 톤을 NEUTRAL 로 낮춘다`() {
        val cardWithFlag =
            sampleCard(axisOrder = 1, depthLevel = 1)
                .copy(cardRedFlagNotices = listOf("답변 사이에 사실관계가 엇갈린 지점이 있었어요."))
        val report = sampleReport(status = InterviewReportStatus.READY, cards = listOf(cardWithFlag))

        val ui = InterviewReportUiMapper.map(report)

        assertEquals(HeadlineTone.NEUTRAL, ui.headline.tone)
        assertEquals(1, ui.redFlagNotices.size)
    }

    @Test
    fun `카드 레드플래그가 여러 개여도 최대 두 줄까지만 노출한다`() {
        val cards =
            listOf(
                sampleCard(axisOrder = 1, depthLevel = 1).copy(cardRedFlagNotices = listOf("경고1")),
                sampleCard(axisOrder = 2, depthLevel = 1).copy(cardRedFlagNotices = listOf("경고2")),
                sampleCard(axisOrder = 3, depthLevel = 1).copy(cardRedFlagNotices = listOf("경고3")),
            )
        val report = sampleReport(status = InterviewReportStatus.READY, cards = cards)

        val ui = InterviewReportUiMapper.map(report)

        assertEquals(2, ui.redFlagNotices.size)
        assertEquals(listOf("경고1", "경고2"), ui.redFlagNotices)
    }

    @Test
    fun `영상이 만료면 만료 안내 문구를 채우고 URL null 로 둔다`() {
        val report =
            sampleReport(
                status = InterviewReportStatus.READY,
                video = InterviewReportVideo(url = null, expired = true, expiresAt = null),
            )

        val ui = InterviewReportUiMapper.map(report)

        assertNull(ui.video?.url)
        assertEquals(true, ui.video?.expired)
        assertTrue(ui.video?.expiredNotice?.isNotBlank() == true)
    }

    @Test
    fun `카드 순서는 axisOrder depthLevel 오름차순이다`() {
        val cards =
            listOf(
                sampleCard(axisOrder = 2, depthLevel = 1),
                sampleCard(axisOrder = 1, depthLevel = 2),
                sampleCard(axisOrder = 1, depthLevel = 1),
            )
        val report = sampleReport(status = InterviewReportStatus.READY, cards = cards)

        val ui = InterviewReportUiMapper.map(report)

        assertEquals(listOf("질문 1-1", "질문 1-2", "질문 2-1"), ui.cards.map { it.label })
    }

    @Test
    fun `하이라이트 span 범위가 대본 길이를 벗어나도 clamp 해 안전 렌더가 가능하다`() {
        val transcript = "짧은 답변."
        val span =
            HighlightSpan(
                startIndex = 3,
                endIndex = 500,
                tone = HighlightTone.POSITIVE,
                reason = HighlightReason.SUFFICIENT,
                title = "충분함",
                analysis = "잘 답하셨어요.",
                followUpQuestions = emptyList(),
                startSec = null,
                answerTopicTitle = null,
                questionIntentTitle = null,
                questionIntent = null,
            )
        val card =
            sampleCard(axisOrder = 1, depthLevel = 1).copy(
                transcript = transcript,
                highlightSpans = listOf(span),
            )
        val report = sampleReport(status = InterviewReportStatus.READY, cards = listOf(card))

        val ui = InterviewReportUiMapper.map(report)

        val mappedHighlight = ui.cards.single().highlights.single()
        assertEquals(3, mappedHighlight.startIndex)
        assertEquals(transcript.length, mappedHighlight.endIndex)
        assertEquals(HighlightUiTone.POSITIVE, mappedHighlight.tone)
        assertEquals(HighlightUiReason.SUFFICIENT, mappedHighlight.reason)
    }

    private fun sampleReport(
        status: InterviewReportStatus,
        headline: String? = null,
        video: InterviewReportVideo? = null,
        cards: List<InterviewReportCard> = emptyList(),
    ): InterviewReport =
        InterviewReport(
            status = status,
            headline = headline,
            video = video,
            cards = cards,
            script = null,
            guestFeedback = null,
        )

    private fun sampleCard(
        axisOrder: Int,
        depthLevel: Int,
    ): InterviewReportCard =
        InterviewReportCard(
            axisOrder = axisOrder,
            depthLevel = depthLevel,
            questionText = "질문 텍스트",
            transcript = "답변 대본",
            highlightSpans = emptyList(),
            resolutionNotice = null,
            cardRedFlagNotices = null,
            questionIntentTitle = null,
            questionIntent = null,
            scriptSegments = null,
        )
}

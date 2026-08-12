@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport

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
 * Preview 전용 합성 리포트 데이터. 실제 사용자 데이터를 쓰지 않고 비식별 가상 값만 사용한다
 * (CONSTITUTION 사용자 데이터 조항).
 */
internal object PreviewInterviewReport {
    private const val ANSWER_PREFIX = "저는 결제 시스템의 응답 지연 문제를 맡아 개선했어요. "
    private const val ANSWER_GOOD = "캐시를 도입해 평균 응답 시간을 절반으로 줄였습니다."
    private val ANSWER_TRANSCRIPT = ANSWER_PREFIX + ANSWER_GOOD

    private val goodHighlight =
        HighlightUiModel(
            startIndex = ANSWER_PREFIX.length,
            endIndex = ANSWER_TRANSCRIPT.length,
            tone = HighlightUiTone.POSITIVE,
            reason = HighlightUiReason.SUFFICIENT,
            title = "구체적 수치로 설명",
            analysis = "개선 전후를 수치로 비교해 설득력이 높았어요.",
            followUpQuestions = emptyList(),
            startSec = 12f,
            answerTopicTitle = null,
            questionIntentTitle = null,
            questionIntent = null,
        )

    private val offIntentHighlight =
        HighlightUiModel(
            startIndex = 0,
            endIndex = ANSWER_PREFIX.length,
            tone = HighlightUiTone.NEGATIVE,
            reason = HighlightUiReason.OFF_INTENT,
            title = "질문 의도와 다르게 답변",
            analysis = "질문이 물은 트래픽 확장 대응 대신 개인 경험을 설명했어요.",
            followUpQuestions = emptyList(),
            startSec = 20f,
            answerTopicTitle = "결제 응답 개선 경험",
            questionIntentTitle = "트래픽 확장 대응 전략",
            questionIntent = "트래픽이 증가했을 때 병목 지점과 한계를 어떻게 판단할지 묻는 질문입니다.",
        )

    /** 컴포넌트 Preview 재사용용 합성 샘플. */
    val sampleCard: CardUiModel get() = card("질문 1-1")
    val sampleRedFlagCard: CardUiModel get() = card("질문 1-2", redFlag = true)
    val sampleGoodHighlight: HighlightUiModel get() = goodHighlight
    val sampleOffIntentHighlight: HighlightUiModel get() = offIntentHighlight
    val sampleTranscript: String get() = ANSWER_TRANSCRIPT
    val sampleGuestFeedback: GuestFeedbackUiModel
        get() =
            GuestFeedbackUiModel(
                participantCount = 0,
                capacity = 4,
                title = "지인에게 면접 영상 보내기",
                subtitle = "4명에게 영상을 공유하고 태도 분석을 받아보세요!",
            )
    val sampleVideo: VideoUiModel
        get() =
            VideoUiModel(
                url = "https://example.com/v.mp4",
                expired = false,
                expiredNotice = null,
            )

    private fun card(
        label: String,
        redFlag: Boolean = false,
    ): CardUiModel =
        CardUiModel(
            label = label,
            questionText = "트래픽이 10배일 때 가장 치명적인 지점과, 그 임계치를 어떻게 생각하시나요?",
            questionIntentTitle = "트래픽 확장 대응 전략",
            questionIntent = "트래픽이 증가했을 때 발생할 병목 지점과 시스템의 한계를 설명하는 질문입니다.",
            transcript = ANSWER_TRANSCRIPT,
            highlights = listOf(goodHighlight, offIntentHighlight),
            resolutionNotice = null,
            cardRedFlagNotices = if (redFlag) listOf("답변 사이에 사실관계가 엇갈린 지점이 있었어요.") else emptyList(),
        )

    val readyReport: ReportUiModel =
        ReportUiModel(
            headline =
                HeadlineUiModel(
                    text = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
                    tone = HeadlineTone.POSITIVE,
                ),
            redFlagNotices = emptyList(),
            video =
                VideoUiModel(
                    url = "https://example.com/v.mp4",
                    expired = false,
                    expiredNotice = null,
                ),
            cards = listOf(card("질문 1-1"), card("질문 1-2", redFlag = true), card("질문 2-1")),
            guestFeedback =
                GuestFeedbackUiModel(
                    participantCount = 0,
                    capacity = 4,
                    title = "지인에게 면접 영상 보내기",
                    subtitle = "4명에게 영상을 공유하고 태도 분석을 받아보세요!",
                ),
        )

    val expiredReport: ReportUiModel =
        readyReport.copy(
            video =
                VideoUiModel(
                    url = null,
                    expired = true,
                    expiredNotice = "24시간이 지나서 영상이 사라졌어요.",
                ),
            guestFeedback = null,
        )
}

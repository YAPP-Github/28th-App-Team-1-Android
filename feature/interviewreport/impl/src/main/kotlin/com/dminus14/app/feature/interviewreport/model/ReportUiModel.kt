package com.dminus14.app.feature.interviewreport.model

import androidx.compose.runtime.Immutable

/**
 * 리포트 상세 화면이 렌더에 사용하는 UI 모델. 도메인 모델을 그대로 사용하지 않고 UI 렌더에
 * 필요한 파생값(하이라이트 span 정합, 카드 표시 라벨 등)을 미리 계산한 뒤 담는다.
 */
@Immutable
data class ReportUiModel(
    val headline: HeadlineUiModel,
    val redFlagNotices: List<String>,
    val video: VideoUiModel?,
    val cards: List<CardUiModel>,
    /** 지인 피드백 섹션. 리포트 응답에 guestFeedback 이 있을 때만 채워지며 null 이면 섹션을 숨긴다. */
    val guestFeedback: GuestFeedbackUiModel?,
)

/**
 * 지인 피드백 섹션 (기획서 §지인피드백, Figma Node: 443:7050). 시안의 "참여수/정원" 은
 * [participantCount] 와 고정 정원 [capacity] 로 구성한다.
 *
 * [guests] 는 실제로 피드백을 제출한 지인별 태도 평가 목록이다. 비어 있으면 아직 아무도
 * 제출하지 않은 상태로, 탭 · 평가 목록을 숨기고 초대 카드만 보여준다.
 */
@Immutable
data class GuestFeedbackUiModel(
    val participantCount: Int,
    val capacity: Int,
    val title: String,
    val subtitle: String,
    val guests: List<GuestFeedbackPersonUiModel> = emptyList(),
)

/** 지인 한 명이 제출한 피드백. 탭 라벨은 [alias]. */
@Immutable
data class GuestFeedbackPersonUiModel(
    val alias: String,
    val ratings: List<GuestFeedbackRatingUiModel>,
)

/** 지인 피드백 평가 항목 하나 (시선/표정/자세/손동작/목소리 중 하나). */
@Immutable
data class GuestFeedbackRatingUiModel(
    val axis: GuestFeedbackAxisUi,
    val axisLabel: String,
    val levelLabel: String,
    val comment: String,
)

/**
 * UI 렌더용 지인 피드백 평가 항목 코드. 서버가 보내는 axis 원시 문자열을 안전하게 파싱하기
 * 위해 두며, 알 수 없는 값은 [fromRaw] 가 null 을 반환해 해당 평가를 조용히 건너뛴다.
 */
enum class GuestFeedbackAxisUi {
    GAZE,
    EXPRESSION,
    POSTURE,
    GESTURE,
    VOICE,
    ;

    companion object {
        fun fromRaw(raw: String): GuestFeedbackAxisUi? =
            entries.firstOrNull { axis -> axis.name.equals(raw, ignoreCase = true) }
    }
}

/** 상단 한 줄 요약 3분기 (기획서 §2-2). */
@Immutable
data class HeadlineUiModel(
    val text: String,
    val tone: HeadlineTone,
)

enum class HeadlineTone {
    /** 정상, 근거 충분: 긍정 톤. */
    POSITIVE,

    /** 정상·근거 약함 또는 심각 레드플래그: 사실 요약. */
    NEUTRAL,

    /** 분석 부족 안내. */
    INSUFFICIENT,
}

@Immutable
data class VideoUiModel(
    val url: String?,
    val expired: Boolean,
    val expiredNotice: String?,
)

@Immutable
data class CardUiModel(
    /** "질문 {axisOrder}-{depthLevel}" 라벨. */
    val label: String,
    val questionText: String,
    val questionIntentTitle: String?,
    val questionIntent: String?,
    val transcript: String,
    val highlights: List<HighlightUiModel>,
    val resolutionNotice: String?,
    val cardRedFlagNotices: List<String>,
)

@Immutable
data class HighlightUiModel(
    val startIndex: Int,
    val endIndex: Int,
    val tone: HighlightUiTone,
    val reason: HighlightUiReason,
    val title: String,
    val analysis: String,
    val followUpQuestions: List<String>,
    val startSec: Float?,
    val answerTopicTitle: String?,
    val questionIntentTitle: String?,
    val questionIntent: String?,
)

/** UI 렌더용 하이라이트 색 톤 (강점/개선/기타). */
enum class HighlightUiTone {
    POSITIVE,
    NEGATIVE,
    NEUTRAL,
}

/** UI 상세 시트가 depth 2 를 결정할 때 쓰는 사유. */
enum class HighlightUiReason {
    PROBE_WORTHY,
    OFF_INTENT,
    SHALLOW,
    SUFFICIENT,
    UNKNOWN,
}

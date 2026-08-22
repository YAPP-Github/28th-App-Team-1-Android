package com.dminus14.app.domain.model

/**
 * 공유 링크로 조회한 비회원 피드백 진입 결과다.
 *
 * [Open]만 영상 시청과 피드백 작성을 시작할 수 있다. [Unavailable]은 API 실패가 아니라 정상
 * 게이트 판정이며, 후속 UI는 [GuestFeedbackUnavailableReason]에 맞는 안내 후 흐름을 종료한다.
 */
sealed interface GuestFeedbackEntry {
    /**
     * 비회원 피드백을 작성할 수 있는 진입 데이터다.
     *
     * 영상 URL은 민감한 사용자 데이터이므로 진행 중 메모리에서만 사용하고 저장하거나
     * 로그로 남기지 않는다.
     */
    data class Open(
        val requesterName: String,
        val axes: List<GuestFeedbackAxis>,
        val videoUrl: String,
        val submissionOpen: Boolean,
    ) : GuestFeedbackEntry

    /** 피드백 작성 흐름에 진입할 수 없는 정상 게이트 결과다. */
    data class Unavailable(
        val reason: GuestFeedbackUnavailableReason,
    ) : GuestFeedbackEntry
}

/** `OPEN`이 아닌 공유 링크의 작성 불가 사유다. */
enum class GuestFeedbackUnavailableReason {
    PRIVATE,
    EXPIRED,
    FULL,
    ALREADY_SUBMITTED,
}

/** 서버가 지정한 평가 항목의 제품 코드와 표시명이다. */
data class GuestFeedbackAxis(
    val code: GuestFeedbackAxisCode,
    val displayName: String,
)

/** 지인이 평가하는 비언어 태도 항목이다. */
enum class GuestFeedbackAxisCode {
    GAZE,
    EXPRESSION,
    POSTURE,
    GESTURE,
    VOICE,
}

/**
 * 서버로 제출할 비회원 피드백 입력이다.
 *
 * [nickname]과 각 평가의 코멘트는 제출 유스케이스가 양끝 공백을 제거한다. 별칭은 줄바꿈 없이
 * `String.length` 1~12자를 만족해야 하며, 비어 있는 코멘트는 빈 문자열로 정규화한다.
 */
data class GuestFeedbackSubmission(
    val nickname: String,
    val ratings: List<GuestFeedbackRating>,
)

/**
 * 지정 평가 항목 하나의 제출 입력이다.
 *
 * 제출 유스케이스는 [level]이 `1..4`인지, 코멘트의 트리밍된 [String.length]가 100 이하인지
 * 검증한다. 코멘트의 문자 종류는 제한하지 않는다.
 */
data class GuestFeedbackRating(
    val axis: GuestFeedbackAxisCode,
    val level: Int,
    val comment: String?,
)

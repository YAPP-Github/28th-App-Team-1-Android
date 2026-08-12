package com.dminus14.app.domain.model

import java.time.Instant

/**
 * 소유자가 조회하는 지인 피드백 공유 링크의 현재 상태다.
 *
 * 면접당 활성 링크는 1개이며, 최초 생성 후 지정 항목([axes])은 링크에 귀속되어 잠긴다.
 */
data class FeedbackShare(
    val token: String,
    val status: FeedbackShareStatus,
    val axes: List<GuestFeedbackAxisCode>,
    val submittedCount: Int,
    val videoExpiresAt: Instant?,
    val requestedAt: Instant?,
)

/** 공유 링크 상태 — ACTIVE(활성) / INVALIDATED(무효) / PRIVATE(비공개). */
enum class FeedbackShareStatus {
    ACTIVE,
    INVALIDATED,
    PRIVATE,
}

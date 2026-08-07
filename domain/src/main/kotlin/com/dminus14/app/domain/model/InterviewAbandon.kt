package com.dminus14.app.domain.model

/**
 * 면접 세션 중단 처리 결과이다.
 */
data class InterviewAbandon(
    val sessionId: Long,
    val status: String,
    val abandonCause: String,
    val endedAt: String,
    val ticketOutcome: String,
    val reportGenerating: Boolean,
)

package com.dminus14.app.domain.model

enum class InterviewAbandonRequestCause {
    NetworkDisconnect,
    UserExit,
}

sealed interface InterviewAbandonCause {
    data object NetworkDisconnect : InterviewAbandonCause

    data object UserExit : InterviewAbandonCause

    data object HoldExpired : InterviewAbandonCause

    data class Unknown(
        val rawValue: String,
    ) : InterviewAbandonCause
}

sealed interface InterviewTicketOutcome {
    data object Committed : InterviewTicketOutcome

    data object Released : InterviewTicketOutcome

    data class Unknown(
        val rawValue: String,
    ) : InterviewTicketOutcome
}

/** 면접 세션 중단 처리 결과이다. */
data class InterviewAbandon(
    val sessionId: Long,
    val status: InterviewTerminalStatus,
    val abandonCause: InterviewAbandonCause,
    val endedAt: String,
    val ticketOutcome: InterviewTicketOutcome,
    val reportGenerating: Boolean,
)

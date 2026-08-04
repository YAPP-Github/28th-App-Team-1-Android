package com.dminus14.app.domain.model

/**
 * 면접 세션 준비 상태 조회 결과.
 *
 * [InterviewSessionStatusType.READY]일 때 [startedAt], [summaryQuestion]이 채워질 수 있다.
 */
data class InterviewSessionStatus(
    val status: InterviewSessionStatusType,
    val startedAt: String?,
    val summaryQuestion: SummaryQuestion?,
)

/**
 * READY 응답의 요약 질문.
 *
 * [ttsAudio]는 Base64 인코딩된 MP3 바이트다.
 */
data class SummaryQuestion(
    val questionId: Long,
    val ttsAudio: String?,
)

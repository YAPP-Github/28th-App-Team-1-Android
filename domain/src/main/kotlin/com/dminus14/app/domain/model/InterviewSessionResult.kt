package com.dminus14.app.domain.model

/**
 * 면접 세션 생성 접수 결과 (HTTP 202).
 *
 * 질문 Preload 등은 서버에서 비동기로 진행되며, 클라이언트는 [status]가
 * [InterviewSessionStatusType.PROCESSING]인 접수를 받는다.
 */
data class InterviewSessionResult(
    val sessionId: Long,
    val status: InterviewSessionStatusType,
    val statusUrl: String,
)

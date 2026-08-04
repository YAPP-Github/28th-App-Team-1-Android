package com.dminus14.app.domain.model

/**
 * 면접 세션 생성 요청이다.
 *
 * [jdUrl]과 [jdText]는 상호 배타적이다.
 */
data class InterviewSessionRequest(
    val portfolioId: String,
    val jdUrl: String? = null,
    val jdText: String? = null,
    val freeText: String? = null,
)

package com.dminus14.app.domain.model

/**
 * JD URL 유효성 검사 결과.
 *
 * `valid == false`인 경우에도 API 호출 자체는 성공이며,
 * 클라이언트는 JD 본문 직접 입력으로 폴백한다.
 */
data class JdValidationResult(
    val valid: Boolean,
    val reason: String?,
    val message: String?,
)

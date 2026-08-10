package com.dminus14.app.domain.model

/**
 * 포트폴리오와 계정 단위 교체·삭제 정책을 함께 담는다.
 *
 * [isReplaceAvailable]·[isDeleteAvailable]은 계정 단위 값이라 [portfolio]가 없어도 항상 채워진다.
 */
data class PortfolioOverview(
    val portfolio: Portfolio?,
    val isReplaceAvailable: Boolean,
    val nextReplaceAvailableAt: String?,
    val isDeleteAvailable: Boolean,
    val nextDeleteAvailableAt: String?,
)

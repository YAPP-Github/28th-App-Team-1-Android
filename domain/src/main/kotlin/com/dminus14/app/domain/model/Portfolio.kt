package com.dminus14.app.domain.model

/**
 * 내 포트폴리오 목록 항목.
 */
data class Portfolio(
    val portfolioId: String,
    val fileName: String,
    val fileSize: Long,
    val pageCount: Int,
    val status: PortfolioStatus,
    val uploadedAt: String,
)

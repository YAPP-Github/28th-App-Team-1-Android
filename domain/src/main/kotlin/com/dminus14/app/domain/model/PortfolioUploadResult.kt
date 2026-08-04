package com.dminus14.app.domain.model

/**
 * 포트폴리오 PDF 업로드 접수 결과 (HTTP 202).
 *
 * 실제 S3 업로드·파싱은 서버에서 비동기로 진행되며, 클라이언트는 [status]가
 * [PortfolioStatus.PROCESSING]인 접수를 받는다.
 */
data class PortfolioUploadResult(
    val portfolioId: String,
    val status: PortfolioStatus,
    val message: String?,
)

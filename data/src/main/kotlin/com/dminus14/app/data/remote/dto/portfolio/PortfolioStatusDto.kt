@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.portfolio

import com.dminus14.app.domain.model.PortfolioStatus
import com.dminus14.app.domain.model.PortfolioUploadResult
import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/portfolios/{portfolioId}/status
 *
 * 요청 본문은 없다.
 */
data class PortfolioStatusResponseDto(
    @SerializedName("portfolioId")
    val portfolioId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String?,
) {
    fun toDomain(): PortfolioUploadResult =
        PortfolioUploadResult(
            portfolioId = portfolioId,
            status = PortfolioStatus.fromRaw(status),
            message = message,
        )
}

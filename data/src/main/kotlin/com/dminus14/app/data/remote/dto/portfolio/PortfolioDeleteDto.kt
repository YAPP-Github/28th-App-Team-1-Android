@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.portfolio

import com.dminus14.app.domain.model.PortfolioDeleteResult
import com.google.gson.annotations.SerializedName

/**
 * DELETE api/v1/portfolios/{portfolioId}
 *
 * 요청 본문은 없다.
 */
data class PortfolioDeleteResponseDto(
    @SerializedName("portfolioId")
    val portfolioId: String,
    @SerializedName("deletedAt")
    val deletedAt: String,
) {
    fun toDomain(): PortfolioDeleteResult =
        PortfolioDeleteResult(
            portfolioId = portfolioId,
            deletedAt = deletedAt,
        )
}

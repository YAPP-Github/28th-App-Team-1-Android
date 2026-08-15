@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.portfolio

import com.dminus14.app.domain.model.Portfolio
import com.dminus14.app.domain.model.PortfolioOverview
import com.dminus14.app.domain.model.PortfolioStatus
import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/portfolios
 *
 * 요청 본문은 없다.
 */
data class PortfolioListResponseDto(
    @SerializedName("portfolios")
    val portfolios: List<PortfolioDto>,
    @SerializedName("replaceAvailable")
    val replaceAvailable: Boolean? = null,
    @SerializedName("nextAvailableAt")
    val nextAvailableAt: String? = null,
    @SerializedName("deleteAvailable")
    val deleteAvailable: Boolean? = null,
    @SerializedName("nextDeleteAvailableAt")
    val nextDeleteAvailableAt: String? = null,
) {
    /** Gson은 원시 Boolean 필드가 JSON에 없으면 false로 채우므로, 없을 시 안전한 기본값(true)은 여기서 직접 적용한다. */
    fun toDomain(): PortfolioOverview =
        PortfolioOverview(
            portfolio = portfolios.firstOrNull()?.toDomain(),
            isReplaceAvailable = replaceAvailable ?: true,
            nextReplaceAvailableAt = nextAvailableAt,
            isDeleteAvailable = deleteAvailable ?: true,
            nextDeleteAvailableAt = nextDeleteAvailableAt,
        )
}

data class PortfolioDto(
    @SerializedName("portfolioId")
    val portfolioId: String,
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("fileSize")
    val fileSize: Long,
    @SerializedName("pageCount")
    val pageCount: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("uploadedAt")
    val uploadedAt: String?,
    @SerializedName("interviewInProgress")
    val interviewInProgress: Boolean = false,
) {
    fun toDomain(): Portfolio =
        Portfolio(
            portfolioId = portfolioId,
            fileName = fileName,
            fileSize = fileSize,
            pageCount = pageCount,
            status = PortfolioStatus.fromRaw(status),
            uploadedAt = uploadedAt,
            isInterviewInProgress = interviewInProgress,
        )
}

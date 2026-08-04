package com.dminus14.app.data.remote.dto

import com.dminus14.app.domain.model.Portfolio
import com.dminus14.app.domain.model.PortfolioDeleteResult
import com.dminus14.app.domain.model.PortfolioStatus
import com.dminus14.app.domain.model.PortfolioUploadResult
import com.google.gson.annotations.SerializedName

data class PortfolioUploadResponseDto(
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

data class PortfolioListResponseDto(
    @SerializedName("portfolios")
    val portfolios: List<PortfolioDto>,
) {
    fun toDomain(): List<Portfolio> = portfolios.map(PortfolioDto::toDomain)
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
    val uploadedAt: String,
) {
    fun toDomain(): Portfolio =
        Portfolio(
            portfolioId = portfolioId,
            fileName = fileName,
            fileSize = fileSize,
            pageCount = pageCount,
            status = PortfolioStatus.fromRaw(status),
            uploadedAt = uploadedAt,
        )
}

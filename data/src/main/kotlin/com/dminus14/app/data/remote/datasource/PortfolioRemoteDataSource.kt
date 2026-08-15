package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.portfolio.PortfolioDeleteResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioListResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioStatusResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioUploadResponseDto
import java.io.File

interface PortfolioRemoteDataSource {
    suspend fun getPortfolios(): PortfolioListResponseDto

    suspend fun uploadPortfolio(
        file: File,
        fileName: String,
        fileSize: Long?,
        pageCount: Int?,
        contentType: String,
    ): PortfolioUploadResponseDto

    suspend fun getPortfolioStatus(portfolioId: String): PortfolioStatusResponseDto

    suspend fun deletePortfolio(portfolioId: String): PortfolioDeleteResponseDto
}

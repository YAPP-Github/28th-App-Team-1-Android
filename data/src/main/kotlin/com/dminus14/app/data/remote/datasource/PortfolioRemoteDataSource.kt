package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.PortfolioDeleteResponseDto
import com.dminus14.app.data.remote.dto.PortfolioListResponseDto
import com.dminus14.app.data.remote.dto.PortfolioUploadResponseDto
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

    suspend fun getPortfolioStatus(portfolioId: String): PortfolioUploadResponseDto

    suspend fun deletePortfolio(portfolioId: String): PortfolioDeleteResponseDto
}

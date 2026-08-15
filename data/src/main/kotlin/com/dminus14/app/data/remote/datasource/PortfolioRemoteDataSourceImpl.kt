package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.PortfolioApi
import com.dminus14.app.data.remote.dto.portfolio.PortfolioDeleteResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioListResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioStatusResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioUploadResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.multipart.PortfolioPartFactory
import com.dminus14.app.domain.exception.ServerException
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRemoteDataSourceImpl
    @Inject
    constructor(
        private val portfolioApi: PortfolioApi,
        private val portfolioPartFactory: PortfolioPartFactory,
    ) : PortfolioRemoteDataSource {
        override suspend fun getPortfolios(): PortfolioListResponseDto {
            val response = portfolioApi.getPortfolios()
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "포트폴리오 목록 응답이 비어 있습니다.",
                )
        }

        override suspend fun uploadPortfolio(
            file: File,
            fileName: String,
            fileSize: Long?,
            pageCount: Int?,
            contentType: String,
        ): PortfolioUploadResponseDto {
            val part = portfolioPartFactory.createPdfPart(file = file, fileName = fileName)
            val response =
                portfolioApi.uploadPortfolio(
                    fileName = fileName,
                    fileSize = fileSize,
                    pageCount = pageCount,
                    contentType = contentType,
                    file = part,
                )
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "포트폴리오 업로드 응답이 비어 있습니다.",
                )
        }

        override suspend fun getPortfolioStatus(portfolioId: String): PortfolioStatusResponseDto {
            val response = portfolioApi.getPortfolioStatus(portfolioId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "포트폴리오 상태 응답이 비어 있습니다.",
                )
        }

        override suspend fun deletePortfolio(portfolioId: String): PortfolioDeleteResponseDto {
            val response = portfolioApi.deletePortfolio(portfolioId)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "포트폴리오 삭제 응답이 비어 있습니다.",
                )
        }
    }

package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.common.ApiResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioDeleteResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioListResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioStatusResponseDto
import com.dminus14.app.data.remote.dto.portfolio.PortfolioUploadResponseDto
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PortfolioApi {
    /**
     * 포트폴리오 PDF 업로드 접수.
     *
     * 성공 시 HTTP 202 + `data.status = PROCESSING`.
     */
    @Multipart
    @POST("api/v1/portfolios")
    suspend fun uploadPortfolio(
        @Query("fileName") fileName: String,
        @Query("fileSize") fileSize: Long?,
        @Query("pageCount") pageCount: Int?,
        @Query("contentType") contentType: String,
        @Part file: MultipartBody.Part,
    ): ApiResponseDto<PortfolioUploadResponseDto>

    /**
     * 내 포트폴리오 목록 조회.
     *
     * MVP는 계정당 1개지만 확장 대비 배열로 반환한다.
     */
    @GET("api/v1/portfolios")
    suspend fun getPortfolios(): ApiResponseDto<PortfolioListResponseDto>

    /**
     * 포트폴리오 처리 상태 조회.
     *
     * 업로드 접수 후 3~5초 간격 폴링용.
     */
    @GET("api/v1/portfolios/{portfolioId}/status")
    suspend fun getPortfolioStatus(
        @Path("portfolioId") portfolioId: String,
    ): ApiResponseDto<PortfolioStatusResponseDto>

    /**
     * 포트폴리오 삭제.
     */
    @DELETE("api/v1/portfolios/{portfolioId}")
    suspend fun deletePortfolio(
        @Path("portfolioId") portfolioId: String,
    ): ApiResponseDto<PortfolioDeleteResponseDto>
}

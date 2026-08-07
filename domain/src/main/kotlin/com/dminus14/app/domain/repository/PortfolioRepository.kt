package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.PortfolioDeleteResult
import com.dminus14.app.domain.model.PortfolioOverview
import com.dminus14.app.domain.model.PortfolioUploadResult
import java.io.File

interface PortfolioRepository {
    /**
     * 내 포트폴리오와 계정 단위 교체·삭제 정책을 함께 조회한다.
     *
     * Access Token은 네트워크 레이어에서 자동 부착된다.
     * MVP는 계정당 포트폴리오 1개다.
     */
    suspend fun getPortfolioOverview(): PortfolioOverview

    /**
     * 포트폴리오 PDF를 멀티파트로 업로드 접수한다.
     *
     * Access Token은 네트워크 레이어에서 자동 부착된다.
     * 성공 시 HTTP 202와 함께 [PortfolioUploadResult.status]가
     * [com.dminus14.app.domain.model.PortfolioStatus.PROCESSING]으로 온다.
     */
    suspend fun uploadPortfolio(
        file: File,
        fileName: String,
        fileSize: Long?,
        pageCount: Int?,
        contentType: String,
    ): PortfolioUploadResult

    /**
     * 포트폴리오 처리 상태를 조회한다.
     *
     * 업로드 접수 후 폴링(권장 3~5초)에 사용한다.
     */
    suspend fun getPortfolioStatus(portfolioId: String): PortfolioUploadResult

    /**
     * 포트폴리오를 삭제한다.
     *
     * Access Token은 네트워크 레이어에서 자동 부착된다.
     */
    suspend fun deletePortfolio(portfolioId: String): PortfolioDeleteResult
}

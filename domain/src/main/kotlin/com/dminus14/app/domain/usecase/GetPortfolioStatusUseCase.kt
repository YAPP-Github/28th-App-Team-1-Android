package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.PortfolioUploadResult
import com.dminus14.app.domain.repository.PortfolioRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 업로드 접수 후 처리 상태 폴링(권장 3~5초)에 사용한다. */
class GetPortfolioStatusUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(portfolioId: String): Result<PortfolioUploadResult> =
            runCatchingCancellable { portfolioRepository.getPortfolioStatus(portfolioId) }
    }

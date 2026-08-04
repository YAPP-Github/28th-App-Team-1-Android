package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.PortfolioDeleteResult
import com.dminus14.app.domain.repository.PortfolioRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class DeletePortfolioUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(portfolioId: String): Result<PortfolioDeleteResult> =
            runCatchingCancellable { portfolioRepository.deletePortfolio(portfolioId) }
    }

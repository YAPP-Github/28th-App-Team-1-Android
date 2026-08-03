package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.Portfolio
import com.dminus14.app.domain.repository.PortfolioRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** MVP는 계정당 포트폴리오 1개라, 목록의 첫 항목을 "내 포트폴리오"로 취급한다. */
class GetPortfolioIdUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(): Result<Portfolio?> =
            runCatchingCancellable { portfolioRepository.getPortfolios().firstOrNull() }
    }

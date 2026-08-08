package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.PortfolioOverview
import com.dminus14.app.domain.repository.PortfolioRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 포트폴리오와 계정 단위 교체·삭제 정책을 함께 조회한다. */
class GetPortfolioOverviewUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(): Result<PortfolioOverview> =
            runCatchingCancellable { portfolioRepository.getPortfolioOverview() }
    }

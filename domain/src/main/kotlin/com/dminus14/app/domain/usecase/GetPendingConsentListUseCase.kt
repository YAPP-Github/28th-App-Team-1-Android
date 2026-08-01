package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.PendingConsentList
import com.dminus14.app.domain.repository.ConsentRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 지금 동의가 필요한 항목 목록과 게이트 상태를 조회한다.
 *
 * [PendingConsentList.status]가 `UP_TO_DATE`이면 약관 화면을 건너뛸 수 있다.
 */
class GetPendingConsentListUseCase
    @Inject
    constructor(
        private val consentRepository: ConsentRepository,
    ) {
        suspend operator fun invoke(): Result<PendingConsentList> =
            runCatchingCancellable { consentRepository.getPendingConsentList() }
    }

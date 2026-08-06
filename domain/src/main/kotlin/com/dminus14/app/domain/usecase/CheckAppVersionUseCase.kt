package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.AppVersionPolicy
import com.dminus14.app.domain.repository.AppVersionRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** [version]의 강제/권장 업데이트 여부를 조회한다. */
class CheckAppVersionUseCase
    @Inject
    constructor(
        private val appVersionRepository: AppVersionRepository,
    ) {
        suspend operator fun invoke(version: String): Result<AppVersionPolicy> =
            runCatchingCancellable { appVersionRepository.checkAppVersion(version) }
    }

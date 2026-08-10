package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class RetryPendingInterviewCleanupUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val clearInterviewLocalData: ClearInterviewLocalDataUseCase,
    ) {
        suspend operator fun invoke() {
            if (repository.isCleanupPending()) clearInterviewLocalData()
        }
    }

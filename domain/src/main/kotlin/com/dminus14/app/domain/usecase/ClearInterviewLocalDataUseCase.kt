package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewWorkController
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class ClearInterviewLocalDataUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val workController: InterviewWorkController,
    ) {
        suspend operator fun invoke() {
            runCatchingCancellable {
                workController.cancelAll()
                repository.clearAll()
                repository.setCleanupPending(false)
            }.getOrElse { error ->
                runCatchingCancellable { repository.setCleanupPending(true) }
                throw error
            }
        }
    }

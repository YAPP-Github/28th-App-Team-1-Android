package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewWorkController
import javax.inject.Inject

class ScheduleInterviewRetentionCleanupUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val workController: InterviewWorkController,
    ) {
        suspend operator fun invoke() {
            val deadlines =
                listOfNotNull(repository.getProgress()?.retentionDeadlineEpochMillis) +
                    repository.getUploadTasks().map { task -> task.retentionDeadlineEpochMillis }
            deadlines.minOrNull()?.let { earliestDeadline ->
                workController.enqueueRetentionCleanup(earliestDeadline)
            }
        }
    }

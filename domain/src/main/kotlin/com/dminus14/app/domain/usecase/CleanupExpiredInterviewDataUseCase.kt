package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewWorkController
import com.dminus14.app.domain.time.InterviewClock
import com.dminus14.app.domain.time.InterviewTimeCalculator
import javax.inject.Inject

class CleanupExpiredInterviewDataUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val workController: InterviewWorkController,
        private val clock: InterviewClock,
        private val timeCalculator: InterviewTimeCalculator,
    ) {
        suspend operator fun invoke() {
            val currentEpochMillis = clock.currentEpochMillis()
            repository.getProgress()?.let { progress ->
                val expired =
                    timeCalculator.retentionRemainingMillis(
                        progress = progress,
                        currentEpochMillis = currentEpochMillis,
                        currentElapsedRealtimeMillis = clock.elapsedRealtimeMillis(),
                    ) == 0L
                if (expired) repository.deleteSession(progress.sessionId)
            }
            repository
                .getUploadTasks()
                .filter { it.retentionDeadlineEpochMillis <= currentEpochMillis }
                .forEach { task ->
                    workController.cancelUpload(task.uploadTaskId)
                    repository.deleteUploadTask(task.uploadTaskId)
                }
        }
    }

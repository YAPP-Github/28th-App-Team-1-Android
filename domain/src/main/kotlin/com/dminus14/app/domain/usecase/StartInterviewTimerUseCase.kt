package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.time.InterviewClock
import javax.inject.Inject

class StartInterviewTimerUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val clock: InterviewClock,
    ) {
        suspend operator fun invoke() {
            val progress = repository.getProgress() ?: return
            if (progress.timerStartedAtEpochMillis != null) return
            val now = clock.currentEpochMillis()
            repository.saveProgress(
                progress.copy(
                    timerStartedAtEpochMillis = now,
                    elapsedAtCheckpointMillis = 0L,
                    checkpointedAtEpochMillis = now,
                ),
            )
        }
    }

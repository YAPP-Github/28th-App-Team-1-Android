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
            val now = clock.currentEpochMillis()
            val realtime = clock.elapsedRealtimeMillis()
            repository.updateProgress { progress ->
                if (progress.timerStartedAtEpochMillis != null) {
                    progress
                } else {
                    progress.copy(
                        timerStartedAtEpochMillis = now,
                        elapsedAtCheckpointMillis = 0L,
                        checkpointedAtEpochMillis = now,
                        elapsedCheckpointElapsedRealtimeMillis = realtime,
                    )
                }
            }
        }
    }

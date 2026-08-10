package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.time.InterviewClock
import com.dminus14.app.domain.time.InterviewTimeCalculator
import javax.inject.Inject

class CheckpointInterviewProgressUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val clock: InterviewClock,
        private val calculator: InterviewTimeCalculator,
    ) {
        suspend operator fun invoke() {
            val progress = repository.getProgress() ?: return
            val epoch = clock.currentEpochMillis()
            val realtime = clock.elapsedRealtimeMillis()
            repository.saveProgress(
                progress.copy(
                    retentionRemainingAtCheckpointMillis =
                        calculator.retentionRemainingMillis(progress, epoch, realtime),
                    retentionCheckpointElapsedRealtimeMillis = realtime,
                    elapsedAtCheckpointMillis = calculator.elapsedMillis(progress, epoch),
                    checkpointedAtEpochMillis = epoch,
                ),
            )
        }
    }

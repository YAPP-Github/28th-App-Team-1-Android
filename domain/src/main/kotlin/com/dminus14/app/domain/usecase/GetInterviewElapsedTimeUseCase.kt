package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.time.InterviewClock
import com.dminus14.app.domain.time.InterviewTimeCalculator
import javax.inject.Inject

class GetInterviewElapsedTimeUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val clock: InterviewClock,
        private val calculator: InterviewTimeCalculator,
    ) {
        suspend operator fun invoke(): Long {
            val progress = repository.getProgress() ?: return 0L
            return calculator.elapsedMillis(progress, clock.currentEpochMillis())
        }
    }

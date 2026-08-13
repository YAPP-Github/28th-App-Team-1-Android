package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewProgress
import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.time.InterviewClock
import com.dminus14.app.domain.time.InterviewTimeCalculator
import javax.inject.Inject

/** 세션 생성 직후 sessionId와 24시간 보존 기한을 로컬 진행 상태로 최초 저장한다. */
class SaveInterviewSessionProgressUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val clock: InterviewClock,
    ) {
        suspend operator fun invoke(sessionId: Long) {
            val epoch = clock.currentEpochMillis()
            val realtime = clock.elapsedRealtimeMillis()
            repository.saveProgress(
                InterviewProgress(
                    sessionId = sessionId,
                    retentionDeadlineEpochMillis = epoch + InterviewTimeCalculator.RETENTION_MILLIS,
                    retentionRemainingAtCheckpointMillis = InterviewTimeCalculator.RETENTION_MILLIS,
                    retentionCheckpointElapsedRealtimeMillis = realtime,
                    timerStartedAtEpochMillis = null,
                    elapsedAtCheckpointMillis = null,
                    checkpointedAtEpochMillis = null,
                    elapsedCheckpointElapsedRealtimeMillis = null,
                ),
            )
        }
    }

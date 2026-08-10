package com.dminus14.app.domain.time

import com.dminus14.app.domain.model.InterviewProgress
import javax.inject.Inject
import kotlin.math.min

/** 클라이언트 타이머와 24시간 보존 기한을 감소하지 않도록 계산한다. */
class InterviewTimeCalculator
    @Inject
    constructor() {
        fun elapsedMillis(
            progress: InterviewProgress,
            currentEpochMillis: Long,
        ): Long {
            val checkpointElapsed = progress.elapsedAtCheckpointMillis ?: 0L
            val checkpointEpoch = progress.checkpointedAtEpochMillis
            val wallDelta =
                checkpointEpoch?.let { (currentEpochMillis - it).coerceAtLeast(0L) } ?: 0L
            return (checkpointElapsed + wallDelta).coerceIn(0L, HARD_CAP_MILLIS)
        }

        fun retentionRemainingMillis(
            progress: InterviewProgress,
            currentEpochMillis: Long,
            currentElapsedRealtimeMillis: Long,
        ): Long {
            val epochRemaining = progress.retentionDeadlineEpochMillis - currentEpochMillis
            val monotonicRemaining =
                progress.retentionCheckpointElapsedRealtimeMillis?.let { checkpoint ->
                    if (currentElapsedRealtimeMillis >= checkpoint) {
                        progress.retentionRemainingAtCheckpointMillis -
                            (currentElapsedRealtimeMillis - checkpoint)
                    } else {
                        null
                    }
                }
            return min(epochRemaining, monotonicRemaining ?: epochRemaining).coerceAtLeast(0L)
        }

        companion object {
            const val HARD_CAP_MILLIS = 12L * 60L * 1_000L
            const val RETENTION_MILLIS = 24L * 60L * 60L * 1_000L
        }
    }

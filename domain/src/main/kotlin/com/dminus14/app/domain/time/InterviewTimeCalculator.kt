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
            /** 한 번의 면접에서 클라이언트 타이머가 누적할 수 있는 최대 진행 시간이다. */
            const val HARD_CAP_MILLIS = 12L * 60L * 1_000L

            /** 최초 진행 상태 생성 시 보존 기한과 잔여 시간을 초기화하는 로컬 데이터 보존 기간이다. */
            const val RETENTION_MILLIS = 24L * 60L * 60L * 1_000L
        }
    }

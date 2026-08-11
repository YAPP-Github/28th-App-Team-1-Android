package com.dminus14.app.feature.interview.interview

import com.dminus14.app.feature.interview.InterviewConstants
import javax.inject.Inject

/** 저장된 경과 시간에서 UI 경계 사건을 한 번씩만 만든다. */
class InterviewTimerCoordinator
    @Inject
    constructor() {
        private var lastElapsedSeconds = 0

        /** 복구 시 이미 지난 경계를 다시 발행하지 않도록 저장된 경과 시간과 동기화한다. */
        fun restore(elapsedMillis: Long) {
            lastElapsedSeconds =
                (elapsedMillis / MILLIS_PER_SECOND)
                    .toInt()
                    .coerceIn(0, InterviewConstants.MAX_INTERVIEW_SECONDS)
        }

        fun update(elapsedMillis: Long): List<Event> {
            val elapsedSeconds =
                (elapsedMillis / MILLIS_PER_SECOND)
                    .toInt()
                    .coerceIn(lastElapsedSeconds, InterviewConstants.MAX_INTERVIEW_SECONDS)
            val events =
                buildList {
                    addIfCrossed(
                        InterviewConstants.CAN_FINISH_INTERVIEW_SECONDS,
                        elapsedSeconds,
                        Event.EARLY_FINISH,
                    )
                    addIfCrossed(InterviewConstants.WRAP_UP_SECONDS, elapsedSeconds, Event.WRAP_UP)
                    addIfCrossed(
                        InterviewConstants.COUNTDOWN_START_SECONDS,
                        elapsedSeconds,
                        Event.COUNTDOWN,
                    )
                    addIfCrossed(
                        InterviewConstants.MAX_INTERVIEW_SECONDS,
                        elapsedSeconds,
                        Event.HARD_CAP,
                    )
                }
            lastElapsedSeconds = elapsedSeconds
            return events
        }

        private fun MutableList<Event>.addIfCrossed(
            boundary: Int,
            elapsedSeconds: Int,
            event: Event,
        ) {
            if (lastElapsedSeconds < boundary && elapsedSeconds >= boundary) add(event)
        }

        enum class Event { EARLY_FINISH, WRAP_UP, COUNTDOWN, HARD_CAP }

        private companion object {
            const val MILLIS_PER_SECOND = 1_000L
        }
    }

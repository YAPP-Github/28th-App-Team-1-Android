package com.dminus14.app.domain.time

interface InterviewClock {
    fun currentEpochMillis(): Long

    fun elapsedRealtimeMillis(): Long
}

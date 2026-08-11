package com.dminus14.app.data.time

import android.os.SystemClock
import com.dminus14.app.domain.time.InterviewClock
import javax.inject.Inject

class AndroidInterviewClock
    @Inject
    constructor() : InterviewClock {
        override fun currentEpochMillis(): Long = System.currentTimeMillis()

        override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
    }

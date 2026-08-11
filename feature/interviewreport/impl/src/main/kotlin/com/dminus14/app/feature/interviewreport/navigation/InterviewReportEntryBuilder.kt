package com.dminus14.app.feature.interviewreport.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.interviewreport.InterviewReportScreen
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import com.dminus14.app.feature.interviewreport.api.InterviewReportPlayer
import com.dminus14.app.feature.interviewreport.player.InterviewReportPlayerScreen

fun EntryProviderScope<Any>.interviewReportEntryBuilder(
    onNavigateBack: () -> Unit,
    onWatchVideo: (sessionId: Long, startSec: Float?) -> Unit,
    onOpenGuestFeedback: (sessionId: Long) -> Unit,
) {
    entry<InterviewReport> {
        InterviewReportScreen(
            onNavigateBack = onNavigateBack,
            onWatchVideo = onWatchVideo,
            onOpenGuestFeedback = onOpenGuestFeedback,
        )
    }

    entry<InterviewReportPlayer> { key ->
        InterviewReportPlayerScreen(
            sessionId = key.sessionId,
            startSec = key.startSec,
            onNavigateBack = onNavigateBack,
        )
    }
}

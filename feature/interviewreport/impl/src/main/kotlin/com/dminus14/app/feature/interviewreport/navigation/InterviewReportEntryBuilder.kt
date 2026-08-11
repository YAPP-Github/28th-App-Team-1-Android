package com.dminus14.app.feature.interviewreport.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.interviewreport.InterviewReportScreen
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import com.dminus14.app.feature.interviewreport.api.InterviewReportPlayer

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

    entry<InterviewReportPlayer> { _ ->
        // TODO(C3): InterviewReportPlayerScreen 배선.
        InterviewReportScreen(
            onNavigateBack = onNavigateBack,
            onWatchVideo = onWatchVideo,
            onOpenGuestFeedback = onOpenGuestFeedback,
        )
    }
}

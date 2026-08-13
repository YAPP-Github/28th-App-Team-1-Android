package com.dminus14.app.feature.interviewreport.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.interviewreport.InterviewReportScreen
import com.dminus14.app.feature.interviewreport.api.GuestFeedbackRequest
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import com.dminus14.app.feature.interviewreport.api.InterviewReportPlayer
import com.dminus14.app.feature.interviewreport.guestfeedback.GuestFeedbackRequestScreen
import com.dminus14.app.feature.interviewreport.player.InterviewReportPlayerScreen

fun EntryProviderScope<Any>.interviewReportEntryBuilder(
    onNavigateBack: () -> Unit,
    onWatchVideo: (sessionId: Long, startSec: Float?) -> Unit,
    onOpenGuestFeedback: (sessionId: Long) -> Unit,
) {
    entry<InterviewReport> { key ->
        InterviewReportScreen(
            sessionId = key.sessionId,
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

    entry<GuestFeedbackRequest> { key ->
        GuestFeedbackRequestScreen(
            sessionId = key.sessionId,
            onNavigateBack = onNavigateBack,
        )
    }
}

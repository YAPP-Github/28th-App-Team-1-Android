package com.dminus14.app.feature.interview.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.interview.api.InterviewErrorRoute
import com.dminus14.app.feature.interview.api.InterviewRoute
import com.dminus14.app.feature.interview.error.InterviewErrorScreen
import com.dminus14.app.feature.interview.interview.InterviewScreen

fun EntryProviderScope<Any>.interviewEntryBuilder(
    onNavigateHome: () -> Unit,
    onResumeInterview: () -> Unit,
) {
    entry<InterviewRoute> {
        InterviewScreen(onNavigateHome = onNavigateHome)
    }

    entry<InterviewErrorRoute> { key ->
        InterviewErrorScreen(
            errorType = key.errorType,
            onNavigateHome = onNavigateHome,
            onResumeInterview = onResumeInterview,
        )
    }
}

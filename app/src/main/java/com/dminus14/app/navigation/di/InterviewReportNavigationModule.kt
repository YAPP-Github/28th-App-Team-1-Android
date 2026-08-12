package com.dminus14.app.navigation.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.interviewreport.api.GuestFeedbackRequest
import com.dminus14.app.feature.interviewreport.api.InterviewReportPlayer
import com.dminus14.app.feature.interviewreport.navigation.interviewReportEntryBuilder
import com.dminus14.app.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object InterviewReportNavigationModule {
    @IntoSet
    @Provides
    fun provideInterviewReportEntryInstaller(
        navigator: Navigator,
    ): EntryProviderScope<Any>.() -> Unit =
        {
            interviewReportEntryBuilder(
                onNavigateBack = navigator::goBack,
                onWatchVideo = { sessionId, startSec ->
                    navigator.goTo(
                        InterviewReportPlayer(sessionId = sessionId, startSec = startSec),
                    )
                },
                onOpenGuestFeedback = { sessionId ->
                    navigator.goTo(GuestFeedbackRequest(sessionId = sessionId))
                },
            )
        }
}

package com.dminus14.app.navigation.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.home.api.Home
import com.dminus14.app.feature.interview.api.InterviewErrorRoute
import com.dminus14.app.feature.interview.interview.InterviewCompletionReason
import com.dminus14.app.feature.interview.navigation.interviewEntryBuilder
import com.dminus14.app.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object InterviewNavigationModule {
    @IntoSet
    @Provides
    fun provideInterviewEntryInstaller(navigator: Navigator): EntryProviderScope<Any>.() -> Unit =
        {
            interviewEntryBuilder(
                onNavigateHome = { navigator.replaceAll(Home) },
                onNavigateError = { errorType -> navigator.goTo(InterviewErrorRoute(errorType)) },
                onResumeInterview = navigator::goBack,
                onInterviewEnded = { reason ->
                    if (reason == InterviewCompletionReason.ABANDONED) {
                        navigator.replaceAll(Home)
                    }
                },
                onSttAcknowledged = { navigator.replaceAll(Home) },
            )
        }
}

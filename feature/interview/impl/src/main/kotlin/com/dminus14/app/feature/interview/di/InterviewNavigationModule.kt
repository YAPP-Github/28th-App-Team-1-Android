package com.dminus14.app.feature.interview.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.interview.navigation.interviewEntryBuilder
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
    fun provideInterviewEntryInstaller(): EntryProviderScope<Any>.() -> Unit =
        {
            interviewEntryBuilder(
                onNavigateHome = {},
                onResumeInterview = {},
            )
        }
}

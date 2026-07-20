package com.dminus14.app.feature.onboarding.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.onboarding.navigation.onboardingEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object OnboardingNavigationModule {
    @IntoSet
    @Provides
    fun provideOnboardingEntryInstaller(): EntryProviderScope<Any>.() -> Unit =
        {
            onboardingEntryBuilder()
        }
}

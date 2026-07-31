package com.dminus14.app.feature.home.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.home.navigation.homeEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeNavigationModule {
    @IntoSet
    @Provides
    fun provideHomeEntryInstaller(): EntryProviderScope<Any>.() -> Unit =
        {
            homeEntryBuilder()
        }
}

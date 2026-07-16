package com.dminus14.app.feature.main.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.main.navigation.mainEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object MainNavigationModule {
    @IntoSet
    @Provides
    fun provideMainEntryInstaller(): EntryProviderScope<Any>.() -> Unit =
        {
            mainEntryBuilder()
        }
}

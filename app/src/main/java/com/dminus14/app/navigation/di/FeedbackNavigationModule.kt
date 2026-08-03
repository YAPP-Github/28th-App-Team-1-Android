package com.dminus14.app.navigation.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.feedback.navigation.feedbackEntryBuilder
import com.dminus14.app.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeedbackNavigationModule {
    @IntoSet
    @Provides
    fun provideFeedbackEntryInstaller(navigator: Navigator): EntryProviderScope<Any>.() -> Unit =
        {
            feedbackEntryBuilder(
                goTo = navigator::goTo,
                goBack = navigator::goBack,
            )
        }
}

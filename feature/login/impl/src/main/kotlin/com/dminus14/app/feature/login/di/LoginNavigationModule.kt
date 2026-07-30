package com.dminus14.app.feature.login.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.navigation.loginEntryBuilder
import com.dminus14.app.feature.login.onboarding.onboardingEntryBuilder
import com.dminus14.app.feature.login.splash.splashEntryBuilder
import com.dminus14.app.feature.login.term.termEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginNavigationModule {
    @IntoSet
    @Provides
    fun provideLoginEntryInstaller(
        @GoToNavigation goTo: (Any) -> Unit,
        @ReplaceAllNavigation replaceAll: (Any) -> Unit,
    ): EntryProviderScope<Any>.() -> Unit =
        {
            splashEntryBuilder(onNavigate = replaceAll)
            termEntryBuilder(onNavigate = goTo)
            onboardingEntryBuilder(onNavigate = goTo)
            loginEntryBuilder(onNavigate = replaceAll)
        }
}

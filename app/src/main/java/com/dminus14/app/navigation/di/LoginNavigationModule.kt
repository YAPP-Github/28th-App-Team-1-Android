package com.dminus14.app.navigation.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.login.navigation.loginEntryBuilder
import com.dminus14.app.navigation.Navigator
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
    fun provideLoginEntryInstaller(navigator: Navigator): EntryProviderScope<Any>.() -> Unit =
        {
            loginEntryBuilder(onNavigate = navigator::goTo)
        }
}

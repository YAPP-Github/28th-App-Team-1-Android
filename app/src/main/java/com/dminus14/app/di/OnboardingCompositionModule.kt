package com.dminus14.app.di

import com.dminus14.app.feature.onboarding.OnBoardingIoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object OnboardingCompositionModule {
    @Provides
    @OnBoardingIoDispatcher
    fun provideOnBoardingIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

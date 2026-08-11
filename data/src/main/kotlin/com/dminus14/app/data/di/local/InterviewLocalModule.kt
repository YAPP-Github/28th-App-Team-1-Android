package com.dminus14.app.data.di.local

import com.dminus14.app.data.repository.InterviewLocalRepositoryImpl
import com.dminus14.app.data.time.AndroidInterviewClock
import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.time.InterviewClock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterviewLocalModule {
    @Binds
    @Singleton
    abstract fun bindInterviewLocalRepository(
        impl: InterviewLocalRepositoryImpl,
    ): InterviewLocalRepository

    @Binds
    @Singleton
    abstract fun bindInterviewClock(impl: AndroidInterviewClock): InterviewClock
}

package com.dminus14.app.feature.interview.di

import com.dminus14.app.domain.repository.InterviewWorkController
import com.dminus14.app.feature.interview.work.WorkManagerInterviewWorkController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterviewWorkModule {
    @Binds
    @Singleton
    abstract fun bindWorkController(
        impl: WorkManagerInterviewWorkController,
    ): InterviewWorkController
}

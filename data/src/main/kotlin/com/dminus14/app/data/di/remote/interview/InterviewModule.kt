package com.dminus14.app.data.di.remote.interview

import com.dminus14.app.data.repository.InterviewRepositoryImpl
import com.dminus14.app.domain.repository.InterviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterviewModule {
    @Binds
    @Singleton
    abstract fun bindInterviewRepository(impl: InterviewRepositoryImpl): InterviewRepository
}

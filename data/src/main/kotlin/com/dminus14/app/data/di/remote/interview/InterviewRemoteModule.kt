package com.dminus14.app.data.di.remote.interview

import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterviewRemoteModule {
    @Binds
    @Singleton
    abstract fun bindInterviewRemoteDataSource(
        impl: InterviewRemoteDataSourceImpl,
    ): InterviewRemoteDataSource
}

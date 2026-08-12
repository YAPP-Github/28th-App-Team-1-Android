package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.remote.datasource.FeedbackShareRemoteDataSource
import com.dminus14.app.data.remote.datasource.FeedbackShareRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** FeedbackShare 원격 데이터 소스 구현을 data 계층 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeedbackShareRemoteModule {
    @Binds
    @Singleton
    abstract fun bindFeedbackShareRemoteDataSource(
        impl: FeedbackShareRemoteDataSourceImpl,
    ): FeedbackShareRemoteDataSource
}

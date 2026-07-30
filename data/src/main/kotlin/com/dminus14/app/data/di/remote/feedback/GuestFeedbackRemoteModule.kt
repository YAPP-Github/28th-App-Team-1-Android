package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.remote.datasource.GuestFeedbackRemoteDataSource
import com.dminus14.app.data.remote.datasource.GuestFeedbackRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Guest Feedback 원격 데이터 소스 구현을 data 계층 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class GuestFeedbackRemoteModule {
    /** Retrofit 기반 구현을 애플리케이션 전역의 원격 데이터 소스 계약으로 제공한다. */
    @Binds
    @Singleton
    abstract fun bindGuestFeedbackRemoteDataSource(
        impl: GuestFeedbackRemoteDataSourceImpl,
    ): GuestFeedbackRemoteDataSource
}

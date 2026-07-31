package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.repository.GuestFeedbackRepositoryImpl
import com.dminus14.app.domain.repository.GuestFeedbackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Guest Feedback Repository 구현을 Domain 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class GuestFeedbackRepositoryModule {
    /** 원격 전용 Repository를 애플리케이션 전역의 Domain 계약으로 제공한다. */
    @Binds
    @Singleton
    abstract fun bindGuestFeedbackRepository(
        impl: GuestFeedbackRepositoryImpl,
    ): GuestFeedbackRepository
}

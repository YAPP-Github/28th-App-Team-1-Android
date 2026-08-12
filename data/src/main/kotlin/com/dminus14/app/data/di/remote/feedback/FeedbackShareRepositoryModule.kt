package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.repository.FeedbackShareRepositoryImpl
import com.dminus14.app.domain.repository.FeedbackShareRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** FeedbackShare Repository 구현을 Domain 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeedbackShareRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFeedbackShareRepository(
        impl: FeedbackShareRepositoryImpl,
    ): FeedbackShareRepository
}

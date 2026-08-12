package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.repository.FeedbackShareRepositoryImpl
import com.dminus14.app.domain.repository.FeedbackShareRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 호스트 지인 피드백 공유 링크 Repository 구현을 domain 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeedbackShareModule {
    @Binds
    @Singleton
    abstract fun bindFeedbackShareRepository(
        impl: FeedbackShareRepositoryImpl,
    ): FeedbackShareRepository
}

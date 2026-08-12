package com.dminus14.app.data.di.local

import com.dminus14.app.data.repository.FeedbackShareLocalRepositoryImpl
import com.dminus14.app.domain.repository.FeedbackShareLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 지인 피드백 공유 링크 token 의 기기 저장 Repository 구현을 domain 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeedbackShareLocalModule {
    @Binds
    @Singleton
    abstract fun bindFeedbackShareLocalRepository(
        impl: FeedbackShareLocalRepositoryImpl,
    ): FeedbackShareLocalRepository
}

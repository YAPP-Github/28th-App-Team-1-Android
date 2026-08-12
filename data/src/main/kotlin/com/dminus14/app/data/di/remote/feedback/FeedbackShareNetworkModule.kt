package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.remote.api.FeedbackShareApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 소유자용(FeedbackShare) API 전송 계층을 제공한다.
 *
 * 로그인 사용자 토큰이 필요해 기본 인증 [Retrofit]을 그대로 주입받아 [FeedbackShareApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object FeedbackShareNetworkModule {
    @Provides
    @Singleton
    fun provideFeedbackShareApi(retrofit: Retrofit): FeedbackShareApi =
        retrofit.create(FeedbackShareApi::class.java)
}

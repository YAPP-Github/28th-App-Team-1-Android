package com.dminus14.app.data.di.remote.interview

import com.dminus14.app.data.remote.api.InterviewApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 면접(JD 검증·세션) API 전송 계층을 제공한다.
 *
 * 로그인 사용자 토큰이 필요해 [NetworkModule][com.dminus14.app.data.di.remote.network.NetworkModule]의
 * 기본 인증 [Retrofit]을 그대로 주입받아 [InterviewApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object InterviewNetworkModule {
    @Provides
    @Singleton
    fun provideInterviewApi(retrofit: Retrofit): InterviewApi =
        retrofit.create(InterviewApi::class.java)
}

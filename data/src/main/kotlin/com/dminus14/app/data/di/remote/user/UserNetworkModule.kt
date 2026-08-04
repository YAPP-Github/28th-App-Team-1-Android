package com.dminus14.app.data.di.remote.user

import com.dminus14.app.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 회원 API 전송 계층을 제공한다.
 *
 * [NetworkModule][com.dminus14.app.data.di.remote.network.NetworkModule]의 기본 인증 [Retrofit]을
 * 주입받아 [UserApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object UserNetworkModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
}

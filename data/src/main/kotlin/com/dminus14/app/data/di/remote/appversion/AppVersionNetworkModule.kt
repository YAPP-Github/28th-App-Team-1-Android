package com.dminus14.app.data.di.remote.appversion

import com.dminus14.app.data.remote.api.AppVersionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 앱 버전 확인 API 전송 계층을 제공한다.
 *
 * [NetworkModule][com.dminus14.app.data.di.remote.network.NetworkModule]의 기본 인증 [Retrofit]을
 * 주입받아 [AppVersionApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppVersionNetworkModule {
    @Provides
    @Singleton
    fun provideAppVersionApi(retrofit: Retrofit): AppVersionApi =
        retrofit.create(AppVersionApi::class.java)
}

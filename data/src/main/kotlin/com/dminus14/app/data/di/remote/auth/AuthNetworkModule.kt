package com.dminus14.app.data.di.remote.auth

import com.dminus14.app.data.di.remote.network.AuthRetrofit
import com.dminus14.app.data.remote.api.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 로그인·토큰 재발급 API 전송 계층을 제공한다.
 *
 * DI 순환을 피하기 위한 [AuthRetrofit] 전용 Retrofit은
 * [NetworkModule][com.dminus14.app.data.di.remote.network.NetworkModule]에서 만들고,
 * 여기서는 그것을 주입받아 [AuthApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {
    @Provides
    @Singleton
    fun provideAuthApi(
        @AuthRetrofit retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)
}

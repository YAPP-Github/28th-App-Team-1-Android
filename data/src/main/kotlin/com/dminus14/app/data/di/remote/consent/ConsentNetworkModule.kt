package com.dminus14.app.data.di.remote.consent

import com.dminus14.app.data.remote.api.ConsentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 동의(Consent) API 전송 계층을 제공한다.
 *
 * [NetworkModule][com.dminus14.app.data.di.remote.network.NetworkModule]의 기본 인증 [Retrofit]을
 * 주입받아 [ConsentApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object ConsentNetworkModule {
    @Provides
    @Singleton
    fun provideConsentApi(retrofit: Retrofit): ConsentApi = retrofit.create(ConsentApi::class.java)
}

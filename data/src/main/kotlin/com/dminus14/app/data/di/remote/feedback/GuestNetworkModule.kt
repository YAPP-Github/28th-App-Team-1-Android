package com.dminus14.app.data.di.remote.feedback

import com.dminus14.app.data.di.remote.network.GuestRetrofit
import com.dminus14.app.data.remote.api.GuestFeedbackApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 비회원(Guest) 피드백 API 전송 계층을 제공한다.
 *
 * Guest 전용 [GuestRetrofit] Retrofit은
 * [NetworkModule][com.dminus14.app.data.di.remote.network.NetworkModule]에서 만들고,
 * 여기서는 그것을 주입받아 [GuestFeedbackApi]를 만든다.
 */
@Module
@InstallIn(SingletonComponent::class)
object GuestNetworkModule {
    @Provides
    @Singleton
    fun provideGuestFeedbackApi(
        @GuestRetrofit retrofit: Retrofit,
    ): GuestFeedbackApi = retrofit.create(GuestFeedbackApi::class.java)
}

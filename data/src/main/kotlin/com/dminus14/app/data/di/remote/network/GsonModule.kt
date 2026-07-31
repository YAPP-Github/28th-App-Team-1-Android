package com.dminus14.app.data.di.remote.network

import com.dminus14.app.data.remote.dto.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackSubmitResponseDto
import com.dminus14.app.data.remote.serialization.GuestFeedbackEntryResponseAdapter
import com.dminus14.app.data.remote.serialization.GuestFeedbackSubmitResponseAdapter
import com.dminus14.app.data.remote.serialization.UtcInstantAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Guest Feedback 응답 계약 어댑터가 적용된 Gson을 식별한다.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GuestGson

/**
 * 전역 JSON 정책과 기능별 Gson 확장 구성을 소유하는 Hilt 모듈이다.
 *
 * HTTP 전송 구성을 소유한 [NetworkModule]과 직렬화 구성을 분리해 각 설정의 적용 범위를
 * 명확하게 유지한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object GsonModule {
    /**
     * 모든 JSON API가 공유하는 null 직렬화와 UTC 변환 정책만 구성한다.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Instant::class.java, UtcInstantAdapter())
            .create()

    /**
     * 공용 JSON 정책을 유지하면서 Guest Feedback 응답 계약 어댑터만 추가한 Gson을 구성한다.
     *
     * 기능 전용 검증이 Default, Auth와 Upload Retrofit의 역직렬화에 영향을 주지 않도록
     * [GuestRetrofit]에만 주입한다.
     */
    @Provides
    @Singleton
    @GuestGson
    fun provideGuestGson(gson: Gson): Gson =
        gson
            .newBuilder()
            .registerTypeAdapter(
                GuestFeedbackEntryResponseDto::class.java,
                GuestFeedbackEntryResponseAdapter(),
            ).registerTypeAdapter(
                GuestFeedbackSubmitResponseDto::class.java,
                GuestFeedbackSubmitResponseAdapter(),
            ).create()
}

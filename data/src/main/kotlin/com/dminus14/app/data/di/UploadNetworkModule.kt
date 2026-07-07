package com.dminus14.app.data.di

import com.dminus14.app.data.remote.api.FileUploadApi
import com.dminus14.app.data.remote.config.UploadNetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UploadOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UploadRetrofit

@Module
@InstallIn(SingletonComponent::class)
object UploadNetworkModule {
    @Provides
    @Singleton
    @UploadOkHttpClient
    fun provideUploadOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(UploadNetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(UploadNetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(UploadNetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @UploadRetrofit
    fun provideUploadRetrofit(
        @UploadOkHttpClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(UploadNetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideFileUploadApi(
        @UploadRetrofit retrofit: Retrofit,
    ): FileUploadApi = retrofit.create(FileUploadApi::class.java)
}

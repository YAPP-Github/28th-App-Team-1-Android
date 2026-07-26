package com.dminus14.app.data.di.remote.network

import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.config.NetworkConfig
import com.dminus14.app.data.remote.interceptor.InsertAuthorizationInterceptor
import com.dminus14.app.data.remote.interceptor.InsertInstallationIdInterceptor
import com.dminus14.app.data.remote.interceptor.OkHttpLoggingInterceptorFactory
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
annotation class DefaultOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideOkHttpClient(
        insertAuthorizationInterceptor: InsertAuthorizationInterceptor,
        insertInstallationIdInterceptor: InsertInstallationIdInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(insertAuthorizationInterceptor)
            .addInterceptor(insertInstallationIdInterceptor)
            .addInterceptor(OkHttpLoggingInterceptorFactory.create())
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
}

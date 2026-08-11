package com.dminus14.app.data.di.remote.interview

import com.dminus14.app.data.remote.authenticator.TokenAuthenticator
import com.dminus14.app.data.remote.config.NetworkConfig
import com.dminus14.app.data.remote.interceptor.InsertAuthorizationInterceptor
import com.dminus14.app.data.remote.interceptor.InsertInstallationIdInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InterviewAudioOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InterviewUploadOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object InterviewMediaNetworkModule {
    @Provides
    @Singleton
    @InterviewAudioOkHttpClient
    fun provideInterviewAudioClient(
        authorizationInterceptor: InsertAuthorizationInterceptor,
        installationIdInterceptor: InsertInstallationIdInterceptor,
        authenticator: TokenAuthenticator,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(installationIdInterceptor)
            .authenticator(authenticator)
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @InterviewUploadOkHttpClient
    fun provideInterviewUploadClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
}

package com.dminus14.app.data.di.remote.network

import com.dminus14.app.data.remote.api.AuthApi
import com.dminus14.app.data.remote.authenticator.TokenAuthenticator
import com.dminus14.app.data.remote.config.NetworkConfig
import com.dminus14.app.data.remote.interceptor.InsertAuthorizationInterceptor
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideOkHttpClient(
        insertAuthorizationInterceptor: InsertAuthorizationInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(insertAuthorizationInterceptor)
            .addInterceptor(OkHttpLoggingInterceptorFactory.create())
            .authenticator(tokenAuthenticator)
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

    /**
     * `AuthApi`(로그인/토큰 재발급) 전용 OkHttpClient.
     *
     * `TokenAuthenticator`는 재발급 시 `SessionRepository` → `AuthRemoteDataSource` → `AuthApi`를
     * 다시 호출하므로, `AuthApi`가 [DefaultOkHttpClient]([TokenAuthenticator] 포함)를 사용하면
     * DI 순환 참조가 발생한다. 이를 피하기 위해 `TokenAuthenticator`가 붙지 않은 별도 클라이언트를 둔다.
     */
    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(OkHttpLoggingInterceptorFactory.create())
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(
        @AuthRetrofit retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)
}

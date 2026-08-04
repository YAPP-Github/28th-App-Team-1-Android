package com.dminus14.app.data.di.remote.network

import com.dminus14.app.data.remote.authenticator.TokenAuthenticator
import com.dminus14.app.data.remote.config.NetworkConfig
import com.dminus14.app.data.remote.interceptor.InsertAuthorizationInterceptor
import com.dminus14.app.data.remote.interceptor.InsertInstallationIdInterceptor
import com.dminus14.app.data.remote.interceptor.OkHttpLoggingInterceptorFactory
import com.google.gson.Gson
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

// OkHttp Clients
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GuestOkHttpClient

// Retrofit Client
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GuestRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideOkHttpClient(
        insertAuthorizationInterceptor: InsertAuthorizationInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        insertInstallationIdInterceptor: InsertInstallationIdInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(insertAuthorizationInterceptor)
            .addInterceptor(insertInstallationIdInterceptor)
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
        gson: Gson,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    /**
     * `AuthApi`(로그인/토큰 재발급) 전용 OkHttpClient.
     *
     * `TokenAuthenticator`는 재발급 시 `SessionRepository` → `AuthRemoteDataSource` → `AuthApi`를
     * 다시 호출하므로, `AuthApi`가 [DefaultOkHttpClient]([TokenAuthenticator] 포함)를 사용하면
     * DI 순환 참조가 발생한다. 이를 피하기 위해 `TokenAuthenticator`가 붙지 않은 별도 클라이언트를 둔다.
     *
     * 요청 바디에 `credential` / `refreshToken`이 포함되므로 debug에서도 BODY 로그를 쓰지 않고
     * HEADERS 수준(`createForUpload`)으로 제한한다.
     */
    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(OkHttpLoggingInterceptorFactory.createForUpload())
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    /**
     * Guest Feedback 요청에 설치 ID만 추가하는 비회원 전용 HTTP client를 제공한다.
     *
     * 공유 token, 영상 URL, 질문 원문과 제출 본문이 로그로 노출되지 않도록 로깅 interceptor는
     * 의도적으로 연결하지 않는다. 회원 인증 interceptor, authenticator와 disk cache도 비회원
     * 전송 계약에 포함되지 않으므로 제외한다.
     */
    @Provides
    @Singleton
    @GuestOkHttpClient
    fun provideGuestOkHttpClient(
        insertInstallationIdInterceptor: InsertInstallationIdInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(insertInstallationIdInterceptor)
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    /** Guest 전용 전송 구성과 [GuestGson] 응답 계약을 결합한 Retrofit을 제공한다. */
    @Provides
    @Singleton
    @GuestRetrofit
    fun provideGuestRetrofit(
        @GuestOkHttpClient okHttpClient: OkHttpClient,
        @GuestGson gson: Gson,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
}

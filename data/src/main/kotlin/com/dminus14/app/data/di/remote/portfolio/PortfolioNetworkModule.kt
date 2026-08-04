package com.dminus14.app.data.di.remote.portfolio

import com.dminus14.app.data.remote.api.PortfolioApi
import com.dminus14.app.data.remote.config.PortfolioNetworkConfig
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PortfolioOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PortfolioRetrofit

/**
 * 포트폴리오 PDF 업로드용 전송 계층을 제공한다.
 *
 * 대용량 멀티파트 업로드를 위해 공용 [NetworkModule]보다 긴 read/write 타임아웃을 쓰는
 * 전용 OkHttp/Retrofit을 둔다.
 */
@Module
@InstallIn(SingletonComponent::class)
object PortfolioNetworkModule {
    @Provides
    @Singleton
    @PortfolioOkHttpClient
    fun providePortfolioOkHttpClient(
        insertInstallationIdInterceptor: InsertInstallationIdInterceptor,
        insertAuthorizationInterceptor: InsertAuthorizationInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(insertAuthorizationInterceptor)
            .addInterceptor(insertInstallationIdInterceptor)
            .addInterceptor(OkHttpLoggingInterceptorFactory.createForUpload())
            .connectTimeout(PortfolioNetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(PortfolioNetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(PortfolioNetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @PortfolioRetrofit
    fun providePortfolioRetrofit(
        @PortfolioOkHttpClient okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PortfolioNetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun providePortfolioApi(
        @PortfolioRetrofit retrofit: Retrofit,
    ): PortfolioApi = retrofit.create(PortfolioApi::class.java)
}

package com.dminus14.app.data.remote.interceptor

import com.dminus14.app.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

internal object OkHttpLoggingInterceptorFactory {
    fun create(
        logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT,
    ): Interceptor = createInterceptor(logger, HttpLoggingInterceptor.Level.BODY)

    fun createForUpload(
        logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT,
    ): Interceptor = createInterceptor(logger, HttpLoggingInterceptor.Level.BODY)

    private fun createInterceptor(
        logger: HttpLoggingInterceptor.Logger,
        debugLevel: HttpLoggingInterceptor.Level,
    ): Interceptor =
        HttpLoggingInterceptor(logger).apply {
            redactHeader(HEADER_AUTHORIZATION)
            redactHeader(HEADER_DEVICE_ID)
            level =
                if (BuildConfig.DEBUG) {
                    debugLevel
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }

    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val HEADER_DEVICE_ID = "Device-Id"
}

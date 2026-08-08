package com.dminus14.app.data.remote.interceptor

import com.dminus14.app.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

internal object OkHttpLoggingInterceptorFactory {
    fun create(
        logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT,
    ): Interceptor = createInterceptor(logger)

    fun createForUpload(
        logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT,
    ): Interceptor = createInterceptor(logger)

    private fun createInterceptor(logger: HttpLoggingInterceptor.Logger): Interceptor =
        HttpLoggingInterceptor(logger).apply {
            redactHeader(HEADER_AUTHORIZATION)
            redactHeader(HEADER_DEVICE_ID)
            level =
                if (BuildConfig.DEBUG) {
                    if (BuildConfig.HTTP_LOGGING_BODY == "true") {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.HEADERS
                    }
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }

    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val HEADER_DEVICE_ID = "Device-Id"
}

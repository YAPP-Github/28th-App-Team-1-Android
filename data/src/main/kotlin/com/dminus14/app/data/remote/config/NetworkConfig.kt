package com.dminus14.app.data.remote.config

import com.dminus14.app.data.BuildConfig

internal object NetworkConfig {
    const val BASE_URL = BuildConfig.SERVER_URL

    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}

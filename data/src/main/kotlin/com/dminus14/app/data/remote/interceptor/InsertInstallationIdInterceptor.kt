package com.dminus14.app.data.remote.interceptor

import com.dminus14.app.data.remote.installation.InstallationIdProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertInstallationIdInterceptor
    @Inject
    constructor(
        private val installationIdProvider: InstallationIdProvider,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requiresInstallationId =
                request
                    .header(HEADER_INSTALLATION_ID_REQUIRED)
                    ?.equals(INSTALLATION_ID_REQUIRED_VALUE, ignoreCase = true) == true

            if (request.header(HEADER_INSTALLATION_ID_REQUIRED) == null) {
                return chain.proceed(request)
            }

            val requestBuilder =
                request
                    .newBuilder()
                    .removeHeader(HEADER_INSTALLATION_ID_REQUIRED)

            if (requiresInstallationId) {
                requestBuilder.header(HEADER_DEVICE_ID, installationIdProvider.get())
            }

            return chain.proceed(requestBuilder.build())
        }

        companion object {
            const val HEADER_INSTALLATION_ID_REQUIRED = "X-Installation-Id-Required"
            const val INSTALLATION_ID_REQUIRED_VALUE = "true"
            const val HEADER_DEVICE_ID = "Device-Id"
        }
    }

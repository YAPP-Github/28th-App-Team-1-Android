package com.dminus14.app.data.remote.interceptor

import com.dminus14.app.data.remote.auth.AccessTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertAuthorizationInterceptor
    @Inject
    constructor(
        private val accessTokenProvider: AccessTokenProvider,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = accessTokenProvider.get()
            val request =
                if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain
                        .request()
                        .newBuilder()
                        .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
                        .build()
                }
            return chain.proceed(request)
        }

        private companion object {
            const val HEADER_AUTHORIZATION = "Authorization"
            const val BEARER_PREFIX = "Bearer "
        }
    }

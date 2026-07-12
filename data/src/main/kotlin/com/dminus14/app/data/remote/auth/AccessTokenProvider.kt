package com.dminus14.app.data.remote.auth

import com.dminus14.app.core.crypto.CryptoManager
import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking

/**
 * OkHttp Interceptor용 동기 AccessToken 제공자.
 *
 * 평문 토큰은 프로세스 메모리에만 캐시하며, 로그에 남기지 않는다.
 * 캐시가 아직 초기화되지 않은 경우 DataStore에서 한 번 복호화해 로드한다.
 */
@Singleton
class AccessTokenProvider
    @Inject
    constructor(
        private val localDataSource: LocalDataSource,
        private val cryptoManager: CryptoManager,
    ) {
        private val lock = Any()

        @Volatile
        private var cachedToken: String? = null

        @Volatile
        private var initialized: Boolean = false

        fun get(): String? {
            if (initialized) return cachedToken
            synchronized(lock) {
                if (initialized) return cachedToken
                cachedToken = runBlocking { readDecryptedAccessToken() }
                initialized = true
                return cachedToken
            }
        }

        fun set(token: String) {
            synchronized(lock) {
                cachedToken = token
                initialized = true
            }
        }

        fun clear() {
            synchronized(lock) {
                cachedToken = null
                initialized = true
            }
        }

        private suspend fun readDecryptedAccessToken(): String? {
            val encrypted = localDataSource.getString(DataStoreKeys.Auth.ACCESS_TOKEN) ?: return null
            return try {
                cryptoManager.decryptFromBase64(encrypted)
            } catch (e: kotlin.coroutines.cancellation.CancellationException) {
                throw e
            } catch (_: Throwable) {
                null
            }
        }
    }

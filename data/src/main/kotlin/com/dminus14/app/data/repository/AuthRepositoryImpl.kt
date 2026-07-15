package com.dminus14.app.data.repository

import com.dminus14.app.core.crypto.CryptoManager
import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import com.dminus14.app.data.remote.auth.AccessTokenProvider
import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val localDataSource: LocalDataSource,
        private val cryptoManager: CryptoManager,
        private val accessTokenProvider: AccessTokenProvider,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): AuthSession {
            val response = authRemoteDataSource.loginWithKakao(credential)
            return persistAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        override suspend fun refreshToken(refreshToken: String): AuthSession {
            val response = authRemoteDataSource.refreshToken(refreshToken)
            return persistAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        override suspend fun getAuthSession(): AuthSession? {
            val encryptedAccessToken = localDataSource.getString(DataStoreKeys.Auth.ACCESS_TOKEN)
            val encryptedRefreshToken = localDataSource.getString(DataStoreKeys.Auth.REFRESH_TOKEN)
            if (encryptedAccessToken == null || encryptedRefreshToken == null) return null
            return runCatching {
                AuthSession(
                    accessToken = cryptoManager.decryptFromBase64(encryptedAccessToken),
                    refreshToken = cryptoManager.decryptFromBase64(encryptedRefreshToken),
                )
            }.getOrElse { error ->
                when (error) {
                    is SecurityException,
                    is IllegalStateException,
                    is IllegalArgumentException,
                    -> {
                        clearAuthSession()
                        null
                    }

                    else -> {
                        throw error
                    }
                }
            }
        }

        override suspend fun clearAuthSession() {
            localDataSource.remove(DataStoreKeys.Auth.ACCESS_TOKEN)
            localDataSource.remove(DataStoreKeys.Auth.REFRESH_TOKEN)
            accessTokenProvider.clear()
        }

        private suspend fun persistAuthSession(
            accessToken: String,
            refreshToken: String,
        ): AuthSession {
            localDataSource.setString(
                DataStoreKeys.Auth.ACCESS_TOKEN,
                cryptoManager.encryptToBase64(accessToken),
            )
            localDataSource.setString(
                DataStoreKeys.Auth.REFRESH_TOKEN,
                cryptoManager.encryptToBase64(refreshToken),
            )
            accessTokenProvider.set(accessToken)
            return AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }

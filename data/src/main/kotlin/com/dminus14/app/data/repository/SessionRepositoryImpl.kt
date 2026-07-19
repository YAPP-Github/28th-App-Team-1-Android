package com.dminus14.app.data.repository

import com.dminus14.app.core.crypto.CryptoManager
import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import com.dminus14.app.data.local.datasource.PreferenceEdit
import com.dminus14.app.data.remote.auth.AccessTokenProvider
import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val localDataSource: LocalDataSource,
        private val cryptoManager: CryptoManager,
        private val accessTokenProvider: AccessTokenProvider,
    ) : SessionRepository {
        override suspend fun getAuthSession(): AuthSession? {
            val encryptedAccessToken = localDataSource.get(DataStoreKeys.Auth.ACCESS_TOKEN)
            val encryptedRefreshToken = localDataSource.get(DataStoreKeys.Auth.REFRESH_TOKEN)
            if (encryptedAccessToken == null || encryptedRefreshToken == null) return null
            return runCatching {
                AuthSession(
                    accessToken = cryptoManager.decryptStringFromBase64(encryptedAccessToken),
                    refreshToken = cryptoManager.decryptStringFromBase64(encryptedRefreshToken),
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

        override suspend fun refreshToken(refreshToken: String): AuthSession {
            val response = authRemoteDataSource.refreshToken(refreshToken)
            return saveAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }

        override suspend fun saveAuthSession(
            accessToken: String,
            refreshToken: String,
        ): AuthSession {
            localDataSource.editAtomically(
                listOf(
                    PreferenceEdit.Set(
                        DataStoreKeys.Auth.ACCESS_TOKEN,
                        cryptoManager.encryptStringToBase64(accessToken),
                    ),
                    PreferenceEdit.Set(
                        DataStoreKeys.Auth.REFRESH_TOKEN,
                        cryptoManager.encryptStringToBase64(refreshToken),
                    ),
                ),
            )
            accessTokenProvider.set(accessToken)
            return AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }

        override suspend fun clearAuthSession() {
            localDataSource.editAtomically(
                listOf(
                    PreferenceEdit.Remove(DataStoreKeys.Auth.ACCESS_TOKEN),
                    PreferenceEdit.Remove(DataStoreKeys.Auth.REFRESH_TOKEN),
                ),
            )
            accessTokenProvider.clear()
        }
    }

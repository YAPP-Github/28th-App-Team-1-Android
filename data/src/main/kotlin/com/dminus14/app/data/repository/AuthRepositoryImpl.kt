package com.dminus14.app.data.repository

import com.dminus14.app.core.crypto.CryptoManager
import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.model.KakaoAuthException
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
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): Result<AuthSession> =
            try {
                val response = authRemoteDataSource.loginWithKakao(credential)

                localDataSource.setString(
                    DataStoreKeys.Auth.ACCESS_TOKEN,
                    cryptoManager.encryptToBase64(response.accessToken),
                )
                localDataSource.setString(
                    DataStoreKeys.Auth.REFRESH_TOKEN,
                    cryptoManager.encryptToBase64(response.refreshToken),
                )

                Result.success(
                    AuthSession(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken,
                    ),
                )
            } catch (error: KakaoAuthException) {
                Result.failure(error)
            }

        override suspend fun getAuthSession(): AuthSession? {
            val encryptedAccessToken =
                localDataSource.getString(DataStoreKeys.Auth.ACCESS_TOKEN) ?: return null
            val encryptedRefreshToken =
                localDataSource.getString(DataStoreKeys.Auth.REFRESH_TOKEN) ?: return null

            return try {
                AuthSession(
                    accessToken = cryptoManager.decryptFromBase64(encryptedAccessToken),
                    refreshToken = cryptoManager.decryptFromBase64(encryptedRefreshToken),
                )
            } catch (_: SecurityException) {
                clearAuthSession()
                null
            } catch (_: IllegalStateException) {
                clearAuthSession()
                null
            } catch (_: IllegalArgumentException) {
                clearAuthSession()
                null
            }
        }

        override suspend fun clearAuthSession() {
            localDataSource.remove(DataStoreKeys.Auth.ACCESS_TOKEN)
            localDataSource.remove(DataStoreKeys.Auth.REFRESH_TOKEN)
        }
    }

package com.dminus14.app.data.repository

import com.dminus14.app.core.crypto.CryptoManager
import com.dminus14.app.data.local.DataStoreKeys
import com.dminus14.app.data.local.datasource.LocalDataSource
import com.dminus14.app.data.local.datasource.PreferenceEdit
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
) : AuthRepository {
    override suspend fun loginWithKakao(credential: String): AuthSession {
        val response = authRemoteDataSource.loginWithKakao(credential)

        localDataSource.editAtomically(
            listOf(
                PreferenceEdit.Set(
                    DataStoreKeys.Auth.ACCESS_TOKEN,
                    cryptoManager.encryptStringToBase64(response.accessToken),
                ),
                PreferenceEdit.Set(
                    DataStoreKeys.Auth.REFRESH_TOKEN,
                    cryptoManager.encryptStringToBase64(response.refreshToken),
                ),
            ),
        )

        return AuthSession(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
    }

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

    override suspend fun clearAuthSession() {
        localDataSource.editAtomically(
            listOf(
                PreferenceEdit.Remove(DataStoreKeys.Auth.ACCESS_TOKEN),
                PreferenceEdit.Remove(DataStoreKeys.Auth.REFRESH_TOKEN),
            ),
        )
    }
}

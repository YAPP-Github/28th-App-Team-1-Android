package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.AuthSession

interface AuthRepository {
    suspend fun loginWithKakao(credential: String): AuthSession

    suspend fun getAuthSession(): AuthSession?

    suspend fun clearAuthSession()
}

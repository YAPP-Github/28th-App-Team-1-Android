package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.AuthRemoteDataSource
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val authRemoteDataSource: AuthRemoteDataSource,
        private val sessionRepository: SessionRepository,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): AuthSession {
            val response = authRemoteDataSource.loginWithKakao(credential)
            return sessionRepository.saveAuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }
    }

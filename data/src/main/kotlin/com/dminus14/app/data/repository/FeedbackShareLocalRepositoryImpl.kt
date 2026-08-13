package com.dminus14.app.data.repository

import com.dminus14.app.data.local.feedback.FeedbackShareTokenStore
import com.dminus14.app.domain.repository.FeedbackShareLocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackShareLocalRepositoryImpl
    @Inject
    constructor(
        private val tokenStore: FeedbackShareTokenStore,
    ) : FeedbackShareLocalRepository {
        override suspend fun getToken(sessionId: Long): String? = tokenStore.getToken(sessionId)

        override suspend fun saveToken(
            sessionId: Long,
            token: String,
        ) {
            tokenStore.setToken(sessionId, token)
        }

        override suspend fun clearToken(sessionId: Long) {
            tokenStore.clearToken(sessionId)
        }
    }

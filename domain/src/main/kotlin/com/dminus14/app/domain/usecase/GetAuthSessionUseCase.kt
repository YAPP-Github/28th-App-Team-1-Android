package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.SessionRepository
import javax.inject.Inject

class GetAuthSessionUseCase
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
    ) {
        suspend operator fun invoke(): AuthSession? = sessionRepository.getAuthSession()
    }

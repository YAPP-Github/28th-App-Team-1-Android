package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithKakaoUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(): Result<AuthSession> = authRepository.loginWithKakao()
    }

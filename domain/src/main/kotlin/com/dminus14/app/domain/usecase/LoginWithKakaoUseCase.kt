package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class LoginWithKakaoUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(credential: String): Result<AuthSession> =
            runCatchingCancellable { authRepository.loginWithKakao(credential) }
    }

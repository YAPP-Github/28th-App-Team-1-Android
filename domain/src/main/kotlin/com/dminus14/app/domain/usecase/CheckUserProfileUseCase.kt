package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class CheckUserProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(): Result<UserProfile> =
            runCatchingCancellable { userRepository.getUserProfile() }
    }

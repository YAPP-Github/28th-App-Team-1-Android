package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.JdValidationResult
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class ValidateJdUrlUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(jdUrl: String): Result<JdValidationResult> =
            runCatchingCancellable { interviewRepository.validateJdUrl(jdUrl) }
    }

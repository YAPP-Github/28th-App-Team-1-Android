package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewVideoExpiry
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class GetVideoExpiryUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): Result<InterviewVideoExpiry> =
            runCatchingCancellable { interviewRepository.getExpiry(sessionId) }
    }

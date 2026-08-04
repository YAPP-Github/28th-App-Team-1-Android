package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class AbandonInterviewUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            cause: String,
        ): Result<InterviewAbandon> =
            runCatchingCancellable { interviewRepository.abandon(sessionId, cause) }
    }

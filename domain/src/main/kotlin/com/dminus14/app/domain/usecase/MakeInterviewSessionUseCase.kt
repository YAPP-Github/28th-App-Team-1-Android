package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class MakeInterviewSessionUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(
            request: InterviewSessionRequest,
        ): Result<InterviewSessionResult> =
            runCatchingCancellable { interviewRepository.createInterviewSession(request) }
    }

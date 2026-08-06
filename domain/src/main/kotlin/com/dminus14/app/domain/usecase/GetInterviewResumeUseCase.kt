package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewResumeStatus
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class GetInterviewResumeUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): Result<InterviewResumeStatus> =
            runCatchingCancellable { interviewRepository.getResume(sessionId) }
    }

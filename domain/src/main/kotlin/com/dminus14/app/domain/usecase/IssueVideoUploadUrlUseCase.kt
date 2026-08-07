package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewVideoUploadUrl
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class IssueVideoUploadUrlUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): Result<InterviewVideoUploadUrl> =
            runCatchingCancellable { interviewRepository.issueUploadUrl(sessionId) }
    }

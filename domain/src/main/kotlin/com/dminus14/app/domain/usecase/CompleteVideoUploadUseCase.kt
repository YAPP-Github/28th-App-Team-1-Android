package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class CompleteVideoUploadUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            wrapUpStartSec: Float? = null,
            wrapUpEndSec: Float? = null,
        ): Result<Unit> =
            runCatchingCancellable {
                interviewRepository.completeUpload(
                    sessionId = sessionId,
                    wrapUpStartSec = wrapUpStartSec,
                    wrapUpEndSec = wrapUpEndSec,
                )
            }
    }

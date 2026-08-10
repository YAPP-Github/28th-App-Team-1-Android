package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewUploadNetworkPolicy
import com.dminus14.app.domain.model.InterviewUploadTask
import com.dminus14.app.domain.repository.InterviewLocalRepository
import java.util.UUID
import javax.inject.Inject

class PrepareInterviewUploadUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            networkPolicy: InterviewUploadNetworkPolicy,
        ): InterviewUploadTask {
            val progress =
                requireNotNull(repository.getProgress()) { "Interview progress does not exist" }
            require(progress.sessionId == sessionId) { "Interview session does not match progress" }
            return InterviewUploadTask(
                uploadTaskId = UUID.randomUUID().toString(),
                sessionId = sessionId,
                retentionDeadlineEpochMillis = progress.retentionDeadlineEpochMillis,
                networkPolicy = networkPolicy,
            ).also { repository.handoffUploadTask(it) }
        }
    }

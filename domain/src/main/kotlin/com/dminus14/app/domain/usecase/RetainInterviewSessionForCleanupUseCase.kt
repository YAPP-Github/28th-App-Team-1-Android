package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewUploadTask
import com.dminus14.app.domain.model.InterviewUploadTaskStatus
import com.dminus14.app.domain.repository.InterviewLocalRepository
import java.util.UUID
import javax.inject.Inject

/** 종료된 세션의 진행 판정은 제거하되 미디어는 공통 보존 기한까지 정리 대상으로 유지한다. */
class RetainInterviewSessionForCleanupUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(sessionId: Long) {
            val progress = repository.getProgress()
            if (progress?.sessionId != sessionId) return
            if (repository.getManifest(sessionId) == null) {
                repository.clearProgress()
                return
            }
            val existingTask =
                repository.getUploadTasks().firstOrNull { task ->
                    task.sessionId == sessionId && task.status == InterviewUploadTaskStatus.RETAINED
                }
            val task =
                existingTask ?: InterviewUploadTask(
                    uploadTaskId = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    retentionDeadlineEpochMillis = progress.retentionDeadlineEpochMillis,
                    status = InterviewUploadTaskStatus.RETAINED,
                )
            repository.handoffUploadTask(task)
        }
    }

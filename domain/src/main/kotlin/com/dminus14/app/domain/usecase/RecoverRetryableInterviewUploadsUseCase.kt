package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewUploadTaskStatus
import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewWorkController
import com.dminus14.app.domain.time.InterviewClock
import javax.inject.Inject

class RecoverRetryableInterviewUploadsUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val workController: InterviewWorkController,
        private val clock: InterviewClock,
    ) {
        suspend operator fun invoke() {
            repository
                .getUploadTasks()
                .filter { task ->
                    task.status == InterviewUploadTaskStatus.FAILED_RETRYABLE &&
                        task.retentionDeadlineEpochMillis > clock.currentEpochMillis()
                }.forEach { task ->
                    if (!workController.isUploadRunningOrPending(task.uploadTaskId)) {
                        repository.saveUploadTask(
                            task.copy(
                                status =
                                    if (task.mergedVideoRef == null) {
                                        InterviewUploadTaskStatus.PENDING_MERGE
                                    } else {
                                        InterviewUploadTaskStatus.PENDING_UPLOAD
                                    },
                                retryCount = 0,
                            ),
                        )
                        workController.enqueueUpload(task.uploadTaskId, task.networkPolicy)
                    }
                }
        }
    }

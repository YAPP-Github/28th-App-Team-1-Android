package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewWorkController
import javax.inject.Inject

class AcknowledgePendingInterviewUploadGlobalEventUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val workController: InterviewWorkController,
    ) {
        suspend operator fun invoke(deliveryId: String) {
            repository
                .getUploadTasks()
                .filter { it.pendingGlobalEventId == deliveryId }
                .forEach { task ->
                    workController.cancelUpload(task.uploadTaskId)
                    repository.deleteUploadTask(task.uploadTaskId)
                }
        }
    }

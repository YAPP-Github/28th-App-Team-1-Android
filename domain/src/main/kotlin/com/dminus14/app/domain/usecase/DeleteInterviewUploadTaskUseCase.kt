package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewLocalRepository
import com.dminus14.app.domain.repository.InterviewWorkController
import javax.inject.Inject

class DeleteInterviewUploadTaskUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
        private val workController: InterviewWorkController,
    ) {
        suspend operator fun invoke(uploadTaskId: String) {
            workController.cancelUpload(uploadTaskId)
            repository.deleteUploadTask(uploadTaskId)
        }
    }

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewUploadTask
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class UpdateInterviewUploadTaskUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(task: InterviewUploadTask) = repository.saveUploadTask(task)
    }

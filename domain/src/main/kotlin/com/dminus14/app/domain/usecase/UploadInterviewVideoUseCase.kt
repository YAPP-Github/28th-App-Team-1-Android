package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.UploadInterviewVideoCommand
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class UploadInterviewVideoUseCase
    @Inject
    constructor(
        private val repository: InterviewRepository,
    ) {
        suspend operator fun invoke(command: UploadInterviewVideoCommand): Result<Unit> =
            runCatchingCancellable { repository.uploadVideo(command) }
    }

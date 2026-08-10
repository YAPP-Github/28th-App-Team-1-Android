package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class GetInterviewUploadManifestUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(uploadTaskId: String): InterviewMediaManifest? =
            repository.getUploadManifest(uploadTaskId)
    }

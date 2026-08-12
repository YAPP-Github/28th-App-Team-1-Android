package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewMediaManifest
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class GetInterviewMediaManifestUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): InterviewMediaManifest? =
            repository.getManifest(sessionId)
    }

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewMediaFileRef
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class DeleteInterviewMediaFileUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(ref: InterviewMediaFileRef) {
            repository.deleteMediaFile(ref)
        }
    }

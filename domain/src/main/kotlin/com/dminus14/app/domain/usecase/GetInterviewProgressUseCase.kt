package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewProgress
import com.dminus14.app.domain.repository.InterviewLocalRepository
import javax.inject.Inject

class GetInterviewProgressUseCase
    @Inject
    constructor(
        private val repository: InterviewLocalRepository,
    ) {
        suspend operator fun invoke(): InterviewProgress? = repository.getProgress()
    }

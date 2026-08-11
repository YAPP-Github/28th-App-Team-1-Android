package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.model.SubmitInterviewAnswerCommand
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

class SubmitAnswerUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        suspend operator fun invoke(
            command: SubmitInterviewAnswerCommand,
        ): Result<SubmitAnswerResult> =
            runCatchingCancellable { interviewRepository.submitAnswer(command) }
    }

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.repository.InterviewRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import java.io.File
import javax.inject.Inject

class SubmitAnswerUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        @Suppress("LongParameterList")
        suspend operator fun invoke(
            sessionId: Long,
            questionId: Long,
            isWrapUp: Boolean,
            questionAudioStartAt: Float? = null,
            questionAudioEndAt: Float? = null,
            answerStartAt: Float? = null,
            answerEndAt: Float? = null,
            answerDuration: Float? = null,
            endType: String? = null,
            audioFile: File? = null,
        ): Result<SubmitAnswerResult> =
            runCatchingCancellable {
                interviewRepository.submitAnswer(
                    sessionId = sessionId,
                    questionId = questionId,
                    isWrapUp = isWrapUp,
                    questionAudioStartAt = questionAudioStartAt,
                    questionAudioEndAt = questionAudioEndAt,
                    answerStartAt = answerStartAt,
                    answerEndAt = answerEndAt,
                    answerDuration = answerDuration,
                    endType = endType,
                    audioFile = audioFile,
                )
            }
    }

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.InterviewRepository
import javax.inject.Inject

/** 인증이 필요한 질문 음성 스트림 URL을 조합한다. */
class GetQuestionAudioStreamUrlUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        operator fun invoke(
            sessionId: Long,
            questionId: Long,
        ): String = interviewRepository.getAudioStreamUrl(sessionId, questionId)
    }

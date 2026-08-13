package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.FeedbackShareLocalRepository
import javax.inject.Inject

/** sessionId 에 기기 저장된 지인 피드백 공유 링크 token 이 있는지 조회한다. */
class GetSavedFeedbackShareTokenUseCase
    @Inject
    constructor(
        private val feedbackShareLocalRepository: FeedbackShareLocalRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): String? =
            feedbackShareLocalRepository.getToken(sessionId)
    }

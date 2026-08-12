package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.FeedbackShare
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 면접 세션의 지인 피드백 공유 링크 상태와 참여 현황을 조회한다. */
class GetFeedbackShareStatusUseCase
    @Inject
    constructor(
        private val feedbackShareRepository: FeedbackShareRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): Result<FeedbackShare> =
            runCatchingCancellable {
                feedbackShareRepository.getStatus(sessionId)
            }
    }

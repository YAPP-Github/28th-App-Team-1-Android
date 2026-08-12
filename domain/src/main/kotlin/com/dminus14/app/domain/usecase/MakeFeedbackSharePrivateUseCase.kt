package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 지인 피드백 공유 링크를 비공개로 전환한다. 재공개는 지원하지 않는다. */
class MakeFeedbackSharePrivateUseCase
    @Inject
    constructor(
        private val feedbackShareRepository: FeedbackShareRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): Result<Unit> =
            runCatchingCancellable {
                feedbackShareRepository.makePrivate(sessionId)
            }
    }

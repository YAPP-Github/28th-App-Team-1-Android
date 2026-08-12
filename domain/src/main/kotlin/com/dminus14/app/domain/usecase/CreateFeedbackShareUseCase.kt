package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 지인 피드백 공유 링크를 생성한다. 성공 시 딥링크 조립용 token 을 담은 [Result] 를 반환한다.
 */
class CreateFeedbackShareUseCase
    @Inject
    constructor(
        private val feedbackShareRepository: FeedbackShareRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): Result<String> =
            runCatchingCancellable {
                feedbackShareRepository.createShare(sessionId, axes)
            }
    }

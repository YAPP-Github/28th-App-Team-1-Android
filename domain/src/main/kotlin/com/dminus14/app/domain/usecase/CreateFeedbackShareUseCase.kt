package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.FeedbackShareValidationException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 지정 평가 항목으로 지인 피드백 공유 링크를 생성하고 공유 token을 반환한다.
 *
 * 지정 항목은 중복 없이 `1..5`개여야 하며, 위반 시 Repository를 호출하지 않고 검증 오류로
 * 반환한다. 서버가 이미 활성 링크가 있으면 별도 비즈니스 오류(`FEEDBACK_SHARE_ALREADY_EXISTS`)로
 * 실패한다.
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
                if (axes.isEmpty() || axes.size > MAX_AXIS_COUNT) {
                    throw FeedbackShareValidationException("지정 평가 항목 수가 올바르지 않습니다.")
                }
                if (axes.distinct().size != axes.size) {
                    throw FeedbackShareValidationException("지정 평가 항목이 중복되었습니다.")
                }
                feedbackShareRepository.create(sessionId, axes)
            }

        companion object {
            const val MAX_AXIS_COUNT = 5
        }
    }

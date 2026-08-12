package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareLocalRepository
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 지인 피드백 공유 링크를 생성한다.
 *
 * 성공하면 token 을 [FeedbackShareLocalRepository] 에 저장해, 재진입 시 화면이 "피드백 종료하기"
 * 버튼으로 전환할 수 있게 한다. 딥링크 조립용 token 을 담은 [Result] 를 반환한다.
 *
 * 생성이 `FEEDBACK_SHARE_ALREADY_EXISTS` 충돌로 실패했을 때의 복구는 이 use case가 아니라
 * [GuestFeedbackRequestViewModel][com.dminus14.app.feature.interviewreport.guestfeedback.GuestFeedbackRequestViewModel]
 * 이 [GetSavedFeedbackShareTokenUseCase] 로 로컬에 저장된 실제 token 을 다시 찾아 처리한다
 * (여기서 실제 token 없이 placeholder 를 저장하면 그 값으로 딥링크를 잘못 재구성하게 된다).
 */
class CreateFeedbackShareUseCase
    @Inject
    constructor(
        private val feedbackShareRepository: FeedbackShareRepository,
        private val feedbackShareLocalRepository: FeedbackShareLocalRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): Result<String> =
            runCatchingCancellable {
                val token = feedbackShareRepository.createShare(sessionId, axes)
                feedbackShareLocalRepository.saveToken(sessionId, token)
                token
            }
    }

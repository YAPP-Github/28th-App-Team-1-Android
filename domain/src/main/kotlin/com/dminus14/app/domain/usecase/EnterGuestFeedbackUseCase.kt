package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.GuestFeedbackValidationException
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.repository.GuestFeedbackRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 공유 token을 정규화하고 비회원 피드백 진입 결과를 조회한다.
 *
 * 양끝 공백을 제거한 token이 비어 있으면 네트워크를 호출하지 않는다. `OPEN` 외 게이트는 실패가
 * 아닌 [GuestFeedbackEntry.Unavailable]이며, coroutine 취소는 [Result] 실패로 감싸지 않는다.
 */
class EnterGuestFeedbackUseCase
    @Inject
    constructor(
        private val guestFeedbackRepository: GuestFeedbackRepository,
    ) {
        /**
         * [token]의 양끝 공백을 제거해 진입을 조회한다.
         *
         * 빈 token은 Repository 호출 전에 검증 오류로 반환하고 non-OPEN 게이트는
         * [GuestFeedbackEntry.Unavailable] 성공 결과로 반환한다.
         */
        suspend operator fun invoke(token: String): Result<GuestFeedbackEntry> =
            runCatchingCancellable {
                val normalizedToken = token.trim()
                if (normalizedToken.isEmpty()) {
                    throw GuestFeedbackValidationException("공유 token이 비어 있습니다.")
                }
                guestFeedbackRepository.enter(normalizedToken)
            }
    }

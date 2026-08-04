package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.SessionRepository
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 서버 회원 탈퇴가 성공한 뒤 로컬과 메모리의 인증 세션을 삭제한다.
 *
 * 두 작업은 원자적이지 않다. 서버 탈퇴 후 세션 삭제가 실패하면 서버 탈퇴는 되돌릴 수 없는
 * 부분 완료 상태이며, 이 Use Case는 로컬 실패를 숨기지 않고 [Result] 실패로 반환한다.
 */
class WithdrawUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val sessionRepository: SessionRepository,
    ) {
        suspend operator fun invoke(): Result<Unit> =
            runCatchingCancellable {
                userRepository.withdraw()
                sessionRepository.clearAuthSession()
            }
    }

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.repository.SessionRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 서버 Refresh Token 폐기 후 로컬 인증 세션을 삭제한다.
 *
 * 두 작업은 원자적이지 않다. 서버 폐기가 실패해도 기기에 토큰을 남기지 않는 편이 안전하므로
 * 서버 실패는 삼키고 로컬 삭제를 반드시 수행한다. 서버 Refresh Token은 만료로 정리된다.
 * 로컬 삭제 자체가 실패하면 [Result] 실패로 반환하고 숨기지 않는다.
 */
class LogoutUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val sessionRepository: SessionRepository,
        private val clearInterviewLocalData: ClearInterviewLocalDataUseCase,
    ) {
        suspend operator fun invoke(): Result<Unit> =
            runCatchingCancellable {
                runCatchingCancellable { authRepository.logout() }
                runCatchingCancellable { clearInterviewLocalData() }
                sessionRepository.clearAuthSession()
            }
    }

package com.dminus14.app.domain.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 로그아웃·회원 탈퇴 흐름이 면접 로컬 정리 UseCase 없이 조립될 수 없도록 보장한다.
 * [ClearInterviewLocalDataUseCase] 파라미터가 nullable 기본값(`= null`)으로 되돌아가면
 * Kotlin이 기본값 마스크(int)를 받는 합성 생성자를 추가로 생성하므로, 그런 합성 생성자가
 * 없는지 확인해 회귀를 막는다.
 */
class AccountCleanupWiringTest {
    @Test
    fun `LogoutUseCase는 ClearInterviewLocalDataUseCase 기본값 생성자를 갖지 않는다`() {
        assertNoDefaultValueConstructor(LogoutUseCase::class.java)
        assertTrue(hasRequiredParameter(LogoutUseCase::class.java))
    }

    @Test
    fun `WithdrawUserUseCase는 ClearInterviewLocalDataUseCase 기본값 생성자를 갖지 않는다`() {
        assertNoDefaultValueConstructor(WithdrawUserUseCase::class.java)
        assertTrue(hasRequiredParameter(WithdrawUserUseCase::class.java))
    }

    private fun assertNoDefaultValueConstructor(target: Class<*>) {
        val hasSyntheticDefaultConstructor =
            target.declaredConstructors.any { constructor ->
                constructor.parameterTypes.any { it.simpleName == "DefaultConstructorMarker" }
            }
        assertFalse(
            "${target.simpleName}에 kotlin 기본값 생성자가 존재합니다: " +
                "ClearInterviewLocalDataUseCase가 nullable 기본값으로 되돌아갔을 수 있습니다",
            hasSyntheticDefaultConstructor,
        )
    }

    private fun hasRequiredParameter(target: Class<*>): Boolean =
        target.declaredConstructors.any { constructor ->
            constructor.parameterTypes.contains(ClearInterviewLocalDataUseCase::class.java)
        }
}

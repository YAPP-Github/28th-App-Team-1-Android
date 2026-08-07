package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoutUseCaseTest {
    @Test
    fun `서버 폐기가 성공하면 로컬 세션도 삭제된다`() {
        val authRepository = FakeAuthRepository(logoutResult = Result.success(Unit))
        val sessionRepository = FakeSessionRepository()
        val useCase = LogoutUseCase(authRepository, sessionRepository)

        val actual = runBlocking { useCase() }

        assertTrue(actual.isSuccess)
        assertEquals(1, sessionRepository.clearCallCount)
    }

    @Test
    fun `서버 폐기가 실패해도 로컬 세션은 삭제된다`() {
        val authRepository =
            FakeAuthRepository(logoutResult = Result.failure(IllegalStateException("서버 오류")))
        val sessionRepository = FakeSessionRepository()
        val useCase = LogoutUseCase(authRepository, sessionRepository)

        val actual = runBlocking { useCase() }

        assertTrue(actual.isSuccess)
        assertEquals(1, sessionRepository.clearCallCount)
    }

    @Test
    fun `로컬 세션 삭제 자체가 실패하면 Result 실패로 전달된다`() {
        val authRepository = FakeAuthRepository(logoutResult = Result.success(Unit))
        val sessionRepository =
            FakeSessionRepository(clearResult = Result.failure(IllegalStateException("저장소 오류")))
        val useCase = LogoutUseCase(authRepository, sessionRepository)

        val actual = runBlocking { useCase() }

        assertTrue(actual.isFailure)
    }

    private class FakeAuthRepository(
        private val logoutResult: Result<Unit>,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): AuthSession = error("사용하지 않음")

        override suspend fun logout() {
            logoutResult.getOrThrow()
        }
    }

    private class FakeSessionRepository(
        private val clearResult: Result<Unit> = Result.success(Unit),
    ) : SessionRepository {
        var clearCallCount = 0
            private set

        override suspend fun getAuthSession(): AuthSession? = null

        override suspend fun refreshToken(refreshToken: String): AuthSession = error("사용하지 않음")

        override suspend fun saveAuthSession(
            accessToken: String,
            refreshToken: String,
        ): AuthSession = error("사용하지 않음")

        override suspend fun clearAuthSession() {
            clearCallCount += 1
            clearResult.getOrThrow()
        }
    }
}

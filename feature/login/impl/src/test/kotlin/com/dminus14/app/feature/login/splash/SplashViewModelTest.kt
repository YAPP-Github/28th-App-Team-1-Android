package com.dminus14.app.feature.login.splash

import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.repository.SessionRepository
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetAuthSessionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    @Test
    fun `Load 시 세션이 있으면 SessionExists Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    SplashViewModel(
                        GetAuthSessionUseCase(
                            FakeSessionRepository(
                                AuthSession(
                                    accessToken = "access",
                                    refreshToken = "refresh",
                                ),
                            ),
                        ),
                        CheckUserProfileUseCase(FakeUserRepository(sampleUserProfile)),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(SplashEffect.SessionExists, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션이 없으면 SessionNotFound Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    SplashViewModel(
                        GetAuthSessionUseCase(FakeSessionRepository(null)),
                        CheckUserProfileUseCase(FakeUserRepository(sampleUserProfile)),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(SplashEffect.SessionNotFound, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션은 있지만 프로필 조회에 실패하면 SessionNotFound Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    SplashViewModel(
                        GetAuthSessionUseCase(
                            FakeSessionRepository(
                                AuthSession(
                                    accessToken = "access",
                                    refreshToken = "refresh",
                                ),
                            ),
                        ),
                        CheckUserProfileUseCase(FakeUserRepository(profile = null)),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(SplashEffect.SessionNotFound, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    private companion object {
        val sampleUserProfile =
            UserProfile(
                name = "홍길동",
                email = "test@example.com",
                provider = "KAKAO",
                jobRole = "BACKEND",
                jobRoleLabel = "백엔드",
                careerYears = 1,
                remainingTicketCount = 3,
            )
    }

    private class FakeSessionRepository(
        private val session: AuthSession?,
    ) : SessionRepository {
        override suspend fun getAuthSession(): AuthSession? = session

        override suspend fun refreshToken(refreshToken: String): AuthSession =
            error("Not used in SplashViewModelTest")

        override suspend fun saveAuthSession(
            accessToken: String,
            refreshToken: String,
        ): AuthSession = error("Not used in SplashViewModelTest")

        override suspend fun clearAuthSession() = error("Not used in SplashViewModelTest")
    }

    private class FakeUserRepository(
        private val profile: UserProfile?,
    ) : UserRepository {
        override suspend fun getUserProfile(): UserProfile = profile ?: error("존재하지 않는 사용자입니다.")
    }
}

package com.dminus14.app.feature.login.splash

import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.repository.SessionRepository
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetAuthSessionUseCase
import com.dminus14.app.domain.usecase.LoginWithKakaoUseCase
import com.dminus14.app.feature.login.kakao.KakaoLoginException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    @Test
    fun `Load 시 세션과 완성된 프로필이 있으면 ProfileReady Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = sampleSession,
                        profileResult = Result.success(sampleUserProfile),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(SplashEffect.ProfileReady, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션은 있지만 이름이 없으면 OnboardingRequired Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = sampleSession,
                        profileResult = Result.success(sampleUserProfileWithoutName),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(SplashEffect.OnboardingRequired, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션이 없으면 카카오 로그인 버튼을 노출한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = null,
                        profileResult = Result.success(sampleUserProfile),
                    )
                val receivedEffects = mutableListOf<SplashEffect>()
                backgroundScope.launch { viewModel.effect.collect(receivedEffects::add) }

                viewModel.onIntent(SplashIntent.Load)
                advanceUntilIdle()

                assertTrue(viewModel.state.value.showKakaoLoginButton)
                assertEquals(emptyList<SplashEffect>(), receivedEffects)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션은 있지만 프로필이 없으면 ProfileNotFound Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = sampleSession,
                        profileResult =
                            Result.failure(UserNotFoundException(errCode = "USER_NOT_FOUND")),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(SplashEffect.ProfileNotFound, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션은 있지만 네트워크 오류면 ShowNetworkErrorAndExit를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = sampleSession,
                        profileResult =
                            Result.failure(
                                NetworkUnavailableException(errCode = "NETWORK_UNAVAILABLE"),
                            ),
                    )
                val receivedEffects = mutableListOf<SplashEffect>()
                backgroundScope.launch { viewModel.effect.collect(receivedEffects::add) }
                val globalEvent =
                    async(start = CoroutineStart.UNDISPATCHED) { GlobalErrorHandler.events.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(GlobalAppEvent.ShowNetworkErrorAndExit, globalEvent.await())
                assertEquals(emptyList<SplashEffect>(), receivedEffects)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션은 있지만 서버 오류면 ShowServerErrorAndExit를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = sampleSession,
                        profileResult = Result.failure(ServerException(errCode = "SERVER_ERROR")),
                    )
                val receivedEffects = mutableListOf<SplashEffect>()
                backgroundScope.launch { viewModel.effect.collect(receivedEffects::add) }
                val globalEvent =
                    async(start = CoroutineStart.UNDISPATCHED) { GlobalErrorHandler.events.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(GlobalAppEvent.ShowServerErrorAndExit, globalEvent.await())
                assertEquals(emptyList<SplashEffect>(), receivedEffects)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `Load 시 세션은 있지만 알 수 없는 오류면 ShowUnknownError를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = sampleSession,
                        profileResult = Result.failure(UnknownException(errCode = "UNKNOWN")),
                    )
                val receivedEffects = mutableListOf<SplashEffect>()
                backgroundScope.launch { viewModel.effect.collect(receivedEffects::add) }
                val globalEvent =
                    async(start = CoroutineStart.UNDISPATCHED) { GlobalErrorHandler.events.first() }

                viewModel.onIntent(SplashIntent.Load)

                assertEquals(GlobalAppEvent.ShowUnknownError, globalEvent.await())
                assertEquals(emptyList<SplashEffect>(), receivedEffects)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `카카오 로그인 성공 후 완성된 프로필이 있으면 ProfileReady Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = null,
                        profileResult = Result.success(sampleUserProfile),
                        loginResult = Result.success(sampleSession),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.KakaoLoginSucceeded("credential"))

                assertEquals(SplashEffect.ProfileReady, effect.await())
                assertFalse(viewModel.state.value.isLoading)
                assertFalse(viewModel.state.value.showKakaoLoginButton)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `카카오 로그인 성공 후 이름이 없으면 OnboardingRequired Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = null,
                        profileResult = Result.success(sampleUserProfileWithoutName),
                        loginResult = Result.success(sampleSession),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.KakaoLoginSucceeded("credential"))

                assertEquals(SplashEffect.OnboardingRequired, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `카카오 로그인 성공 후 프로필이 없으면 ProfileNotFound Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = null,
                        profileResult =
                            Result.failure(UserNotFoundException(errCode = "USER_NOT_FOUND")),
                        loginResult = Result.success(sampleSession),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(SplashIntent.KakaoLoginSucceeded("credential"))

                assertEquals(SplashEffect.ProfileNotFound, effect.await())
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `카카오 로그인을 취소하면 로딩만 해제한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        session = null,
                        profileResult = Result.success(sampleUserProfile),
                    )
                viewModel.onIntent(SplashIntent.ClickKakaoLogin)
                assertTrue(viewModel.state.value.isLoading)

                viewModel.onIntent(SplashIntent.KakaoLoginFailed(KakaoLoginException.Cancelled))
                advanceUntilIdle()

                assertFalse(viewModel.state.value.isLoading)
                assertNull(viewModel.state.value.errorMessage)
            } finally {
                Dispatchers.resetMain()
            }
        }

    private fun createViewModel(
        session: AuthSession?,
        profileResult: Result<UserProfile>,
        loginResult: Result<AuthSession> = Result.success(sampleSession),
    ): SplashViewModel =
        SplashViewModel(
            GetAuthSessionUseCase(FakeSessionRepository(session)),
            CheckUserProfileUseCase(FakeUserRepository(profileResult)),
            LoginWithKakaoUseCase(FakeAuthRepository(loginResult)),
        )

    private companion object {
        val sampleSession =
            AuthSession(
                accessToken = "access",
                refreshToken = "refresh",
            )

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

        val sampleUserProfileWithoutName =
            sampleUserProfile.copy(
                name = null,
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
        private val result: Result<UserProfile>,
    ) : UserRepository {
        override suspend fun getUserProfile(): UserProfile = result.getOrThrow()
    }

    private class FakeAuthRepository(
        private val loginResult: Result<AuthSession>,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): AuthSession =
            loginResult.getOrThrow()
    }
}

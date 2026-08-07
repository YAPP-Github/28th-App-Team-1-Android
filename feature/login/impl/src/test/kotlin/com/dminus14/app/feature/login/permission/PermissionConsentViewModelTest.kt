package com.dminus14.app.feature.login.permission

import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.permission.AppPermission
import com.dminus14.app.core.permission.PermissionManager
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionConsentViewModelTest {
    @Test
    fun `ClickAllow 시 RequestPermissions Effect를 발행하고 프로필 조회를 호출하지 않는다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val userRepository = FakeUserRepository(Result.success(sampleUserProfile))
                val viewModel = createViewModel(userRepository = userRepository)
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickAllow)

                assertEquals(PermissionConsentEffect.RequestPermissions, effect.await())
                assertFalse(viewModel.state.value.isLoading)
                assertEquals(0, userRepository.getUserProfileCallCount)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 프로필 조회 성공하면 NavigateHome Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository = FakeUserRepository(Result.success(sampleUserProfile)),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(PermissionConsentEffect.NavigateHome, effect.await())
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 UserNotFoundException 이면 NavigateOnboarding Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(
                                Result.failure(
                                    UserNotFoundException(errCode = "USER_NOT_FOUND"),
                                ),
                            ),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(PermissionConsentEffect.NavigateOnboarding, effect.await())
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 프로필 조회 성공했지만 이름이 없으면 NavigateOnboarding Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(
                                Result.success(sampleUserProfile.copy(name = null)),
                            ),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(PermissionConsentEffect.NavigateOnboarding, effect.await())
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 프로필 조회 성공했지만 이메일이 없으면 NavigateOnboarding Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(
                                Result.success(sampleUserProfile.copy(email = null)),
                            ),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(PermissionConsentEffect.NavigateOnboarding, effect.await())
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 NetworkUnavailableException 이면 ShowNetworkErrorAndExit를 emit한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(
                                Result.failure(
                                    NetworkUnavailableException(errCode = "NETWORK_UNAVAILABLE"),
                                ),
                            ),
                    )
                val receivedEffects = collectEffects(viewModel)
                val globalEvent =
                    async(start = CoroutineStart.UNDISPATCHED) { GlobalErrorHandler.events.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(GlobalAppEvent.ShowNetworkErrorAndExit, globalEvent.await())
                assertEquals(emptyList<PermissionConsentEffect>(), receivedEffects)
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 ServerException 이면 ShowServerErrorAndExit를 emit한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(
                                Result.failure(ServerException(errCode = "INTERNAL_ERROR")),
                            ),
                    )
                val receivedEffects = collectEffects(viewModel)
                val globalEvent =
                    async(start = CoroutineStart.UNDISPATCHED) { GlobalErrorHandler.events.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(GlobalAppEvent.ShowServerErrorAndExit, globalEvent.await())
                assertEquals(emptyList<PermissionConsentEffect>(), receivedEffects)
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 시 알 수 없는 예외면 ShowUnknownError를 emit한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(Result.failure(IllegalStateException("boom"))),
                    )
                val receivedEffects = collectEffects(viewModel)
                val globalEvent =
                    async(start = CoroutineStart.UNDISPATCHED) { GlobalErrorHandler.events.first() }

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                assertEquals(GlobalAppEvent.ShowUnknownError, globalEvent.await())
                assertEquals(emptyList<PermissionConsentEffect>(), receivedEffects)
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `ClickLater 진행 중에는 isLoading이 true이며 완료 시 false로 돌아온다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val gate = CompletableDeferred<UserProfile>()
                val userRepository = ControllableUserRepository(gate)
                val viewModel = createViewModel(userRepository = userRepository)

                viewModel.onIntent(PermissionConsentIntent.ClickLater)

                // 프로필 조회가 아직 완료되지 않아 로딩 중이다.
                assertTrue(viewModel.state.value.isLoading)

                gate.complete(sampleUserProfile)
                advanceUntilIdle()

                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `PermissionResult 모두 허용이고 프로필 성공이면 NavigateHome Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository = FakeUserRepository(Result.success(sampleUserProfile)),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.PermissionResult(allGranted = true))

                assertEquals(PermissionConsentEffect.NavigateHome, effect.await())
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `PermissionResult 모두 허용이고 UserNotFoundException 이면 NavigateOnboarding Effect를 발행한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel =
                    createViewModel(
                        userRepository =
                            FakeUserRepository(
                                Result.failure(
                                    UserNotFoundException(errCode = "USER_NOT_FOUND"),
                                ),
                            ),
                    )
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.PermissionResult(allGranted = true))

                assertEquals(PermissionConsentEffect.NavigateOnboarding, effect.await())
                assertFalse(viewModel.state.value.isLoading)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `PermissionResult 하나 이상 거부면 NavigateDenied Effect를 발행하고 프로필을 조회하지 않는다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val userRepository = FakeUserRepository(Result.success(sampleUserProfile))
                val viewModel = createViewModel(userRepository = userRepository)
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(PermissionConsentIntent.PermissionResult(allGranted = false))

                assertEquals(PermissionConsentEffect.NavigateDenied, effect.await())
                assertFalse(viewModel.state.value.isLoading)
                assertEquals(0, userRepository.getUserProfileCallCount)
            } finally {
                Dispatchers.resetMain()
            }
        }

    private fun TestScope.collectEffects(
        viewModel: PermissionConsentViewModel,
    ): MutableList<PermissionConsentEffect> {
        val receivedEffects = mutableListOf<PermissionConsentEffect>()
        backgroundScope.launch { viewModel.effect.collect(receivedEffects::add) }
        return receivedEffects
    }

    private fun createViewModel(userRepository: UserRepository): PermissionConsentViewModel =
        PermissionConsentViewModel(
            CheckUserProfileUseCase(userRepository),
            FakePermissionManager(),
        )

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

    private class FakeUserRepository(
        private val result: Result<UserProfile>,
    ) : UserRepository {
        var getUserProfileCallCount: Int = 0
            private set

        override suspend fun getUserProfile(): UserProfile {
            getUserProfileCallCount += 1
            return result.getOrThrow()
        }

        override suspend fun updateUserProfile(update: UserProfileUpdate) =
            error("Not used in PermissionConsentViewModelTest")

        override suspend fun withdraw() = error("Not used in PermissionConsentViewModelTest")
    }

    private class ControllableUserRepository(
        private val gate: CompletableDeferred<UserProfile>,
    ) : UserRepository {
        override suspend fun getUserProfile(): UserProfile = gate.await()

        override suspend fun updateUserProfile(update: UserProfileUpdate) =
            error("Not used in PermissionConsentViewModelTest")

        override suspend fun withdraw() = error("Not used in PermissionConsentViewModelTest")
    }

    private class FakePermissionManager : PermissionManager {
        override fun isGranted(permission: AppPermission): Boolean = false

        override fun shouldShowRationale(
            permission: AppPermission,
            shouldShowRequestPermissionRationale: (manifestPermission: String) -> Boolean,
        ): Boolean = false
    }
}

package com.dminus14.app.feature.login.login

import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.AuthSession
import com.dminus14.app.domain.repository.AuthRepository
import com.dminus14.app.domain.usecase.LoginWithKakaoUseCase
import com.dminus14.app.feature.login.kakao.KakaoLoginException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @Test
    fun `ClickKakaoLogin Intent는 로딩 상태로 전환하고 에러를 초기화한다`() =
        runTest {
            withViewModel(FakeAuthRepository.success()) { viewModel ->
                viewModel.onIntent(LoginIntent.ClickKakaoLogin)

                assertEquals(true, viewModel.state.value.isLoading)
                assertNull(viewModel.state.value.errorMessage)
            }
        }

    @Test
    fun `KakaoLoginSucceeded Intent는 로그인 성공 시 SuccessSocialLogin Effect를 발행한다`() =
        runTest {
            withViewModel(FakeAuthRepository.success()) { viewModel ->
                val effect = async { viewModel.effect.first() }

                viewModel.onIntent(LoginIntent.KakaoLoginSucceeded(credential = "credential"))

                assertEquals(LoginEffect.SuccessSocialLogin, effect.await())
                assertEquals(false, viewModel.state.value.isLoading)
            }
        }

    @Test
    fun `KakaoLoginSucceeded Intent는 로그인 실패 시 에러 메시지를 상태에 반영한다`() =
        runTest {
            val exception = UnknownException(errCode = "UNKNOWN")
            withViewModel(FakeAuthRepository.failure(exception)) { viewModel ->
                viewModel.onIntent(LoginIntent.KakaoLoginSucceeded(credential = "credential"))

                assertEquals(false, viewModel.state.value.isLoading)
                assertEquals(exception.message, viewModel.state.value.errorMessage)
            }
        }

    @Test
    fun `KakaoLoginFailed Intent는 카카오 로그인 취소 시 에러 메시지 없이 로딩만 해제한다`() =
        runTest {
            withViewModel(FakeAuthRepository.success()) { viewModel ->
                viewModel.onIntent(LoginIntent.ClickKakaoLogin)

                viewModel.onIntent(LoginIntent.KakaoLoginFailed(KakaoLoginException.Cancelled))

                assertEquals(false, viewModel.state.value.isLoading)
                assertNull(viewModel.state.value.errorMessage)
            }
        }

    @Test
    fun `KakaoLoginFailed Intent는 카카오 로그인 실패 시 에러 메시지를 상태에 반영한다`() =
        runTest {
            withViewModel(FakeAuthRepository.success()) { viewModel ->
                val exception = KakaoLoginException.Unknown()

                viewModel.onIntent(LoginIntent.KakaoLoginFailed(exception))

                assertEquals(false, viewModel.state.value.isLoading)
                assertEquals(exception.message, viewModel.state.value.errorMessage)
            }
        }

    private suspend fun TestScope.withViewModel(
        authRepository: AuthRepository,
        block: suspend (LoginViewModel) -> Unit,
    ) {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = LoginViewModel(LoginWithKakaoUseCase(authRepository))
            block(viewModel)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeAuthRepository private constructor(
        private val session: AuthSession?,
        private val error: Throwable?,
    ) : AuthRepository {
        override suspend fun loginWithKakao(credential: String): AuthSession =
            error?.let { throw it } ?: requireNotNull(session)

        companion object {
            fun success(): FakeAuthRepository =
                FakeAuthRepository(
                    session = AuthSession(accessToken = "access", refreshToken = "refresh"),
                    error = null,
                )

            fun failure(error: Throwable): FakeAuthRepository =
                FakeAuthRepository(session = null, error = error)
        }
    }
}

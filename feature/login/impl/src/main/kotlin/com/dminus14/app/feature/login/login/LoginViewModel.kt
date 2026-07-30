package com.dminus14.app.feature.login.login

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.usecase.LoginWithKakaoUseCase
import com.dminus14.app.feature.login.kakao.KakaoLoginException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
    ) : MviViewModel<LoginIntent, LoginState, LoginEffect>(LoginState()) {
        override fun onIntent(intent: LoginIntent) {
            when (intent) {
                LoginIntent.ClickKakaoLogin -> {
                    reduce { copy(isLoading = true, errorMessage = null) }
                }

                is LoginIntent.KakaoLoginSucceeded -> {
                    loginWithKakao(intent.credential)
                }

                is LoginIntent.KakaoLoginFailed -> {
                    handleLoginFailure(intent.error)
                }
            }
        }

        private fun loginWithKakao(credential: String) {
            viewModelScope.launch {
                reduce { copy(isLoading = true, errorMessage = null) }

                loginWithKakaoUseCase(credential)
                    .onSuccess {
                        reduce { copy(isLoading = false) }
                        sendEffect(LoginEffect.SuccessSocialLogin)
                    }.onFailure { throwable ->
                        handleLoginFailure(throwable)
                    }
            }
        }

        private fun handleLoginFailure(throwable: Throwable) {
            when (throwable) {
                is KakaoLoginException.Cancelled -> {
                    reduce { copy(isLoading = false) }
                }

                is KakaoLoginException,
                is CustomException,
                -> {
                    reduce {
                        copy(
                            isLoading = false,
                            errorMessage = throwable.message,
                        )
                    }
                }

                else -> {
                    reduce {
                        copy(
                            isLoading = false,
                            errorMessage = throwable.message,
                        )
                    }
                }
            }
        }
    }

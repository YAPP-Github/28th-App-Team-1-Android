package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.modal.GlobalModalRequest
import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.showGlobalModal
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetAuthSessionUseCase
import com.dminus14.app.domain.usecase.LoginWithKakaoUseCase
import com.dminus14.app.feature.login.kakao.KakaoLoginException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getAuthSessionUseCase: GetAuthSessionUseCase,
        private val checkUserProfileUseCase: CheckUserProfileUseCase,
        private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
    ) : MviViewModel<SplashIntent, SplashState, SplashEffect>(SplashState()) {
        override fun onIntent(intent: SplashIntent) {
            when (intent) {
                SplashIntent.Load -> {
                    load()
                }

                SplashIntent.ClickKakaoLogin -> {
                    reduce { copy(isLoading = true, errorMessage = null) }
                }

                is SplashIntent.KakaoLoginSucceeded -> {
                    loginWithKakao(intent.credential)
                }

                is SplashIntent.KakaoLoginFailed -> {
                    handleLoginFailure(intent.error)
                }
            }
        }

        private fun load() {
            viewModelScope.launch {
                getAuthSessionUseCase()
                    .onSuccess { session ->
                        if (session != null) {
                            checkUserProfile()
                        } else {
                            reduce { copy(showKakaoLoginButton = true) }
                        }
                    }.onFailure {
                        reduce { copy(showKakaoLoginButton = true) }
                    }
            }
        }

        private fun loginWithKakao(credential: String) {
            viewModelScope.launch {
                reduce { copy(isLoading = true, errorMessage = null) }

                loginWithKakaoUseCase(credential)
                    .onSuccess {
                        checkUserProfile()
                    }.onFailure { throwable ->
                        handleLoginFailure(throwable)
                    }
            }
        }

        private suspend fun checkUserProfile() {
            checkUserProfileUseCase()
                .onSuccess {
                    reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                    sendEffect(SplashEffect.ProfileExists)
                }.onFailure {
                    when (it) {
                        is UserNotFoundException -> {
                            reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                            sendEffect(SplashEffect.ProfileNotFound)
                        }

                        else -> {
                            reduce { copy(isLoading = false) }
                            // 문구, 확인 이후 후속 동작(재시도/이동 여부)은 기획 확정 후 반영
                            val result =
                                showGlobalModal(
                                    GlobalModalRequest(
                                        title = "오류가 발생했어요",
                                        message = "잠시 후 다시 시도해 주세요.",
                                        confirmText = "확인",
                                        dismissible = false,
                                    ),
                                )

                            when (result) {
                                GlobalModalResult.Confirm -> {
                                    sendEffect(SplashEffect.UnknownError)
                                }

                                else -> {
                                    Unit
                                }
                            }
                        }
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

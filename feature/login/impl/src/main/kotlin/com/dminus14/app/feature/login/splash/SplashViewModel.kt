package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
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
                .onSuccess { profile ->
                    reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                    if (profile.requiresOnboarding) {
                        sendEffect(SplashEffect.OnboardingRequired)
                    } else {
                        sendEffect(SplashEffect.ProfileReady)
                    }
                }.onFailure { error ->
                    when (error) {
                        is UserNotFoundException -> {
                            reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                            sendEffect(SplashEffect.ProfileNotFound)
                        }

                        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
                        is NetworkUnavailableException -> {
                            reduce { copy(isLoading = false) }
                            GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                        }

                        is ServerException -> {
                            reduce { copy(isLoading = false) }
                            GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                        }

                        else -> {
                            reduce { copy(isLoading = false) }
                            GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
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

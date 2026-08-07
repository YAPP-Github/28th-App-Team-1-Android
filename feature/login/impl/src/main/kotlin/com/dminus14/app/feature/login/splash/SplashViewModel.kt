package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.InvalidCredentialException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.SocialLoginFailedException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetAuthSessionUseCase
import com.dminus14.app.domain.usecase.GetPendingConsentListUseCase
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
        private val getPendingConsentListUseCase: GetPendingConsentListUseCase,
    ) : MviViewModel<SplashIntent, SplashState, SplashEffect>(SplashState()) {
        override fun onIntent(intent: SplashIntent) {
            when (intent) {
                SplashIntent.Load -> {
                    load()
                }

                SplashIntent.ClickKakaoLogin -> {
                    reduce { copy(isLoading = true) }
                }

                is SplashIntent.KakaoLoginSucceeded -> {
                    loginWithKakao(intent.credential)
                }

                is SplashIntent.KakaoLoginFailed -> {
                    when (intent.error) {
                        is KakaoLoginException.Cancelled -> {
                            reduce { copy(isLoading = false) }
                        }

                        else -> {
                            reduce { copy(isLoading = false) }
                            sendEffect(
                                SplashEffect.ShowToast(
                                    intent.error.message ?: DEFAULT_LOGIN_ERROR_MESSAGE,
                                ),
                            )
                        }
                    }
                }
            }
        }

        private fun load() {
            viewModelScope.launch {
                getAuthSessionUseCase()
                    .onSuccess { session ->
                        if (session != null) {
                            checkPendingConsent()
                        } else {
                            reduce { copy(isLoading = false, showKakaoLoginButton = true) }
                        }
                    }.onFailure {
                        reduce { copy(isLoading = false, showKakaoLoginButton = true) }
                    }
            }
        }

        private fun loginWithKakao(credential: String) {
            viewModelScope.launch {
                reduce { copy(isLoading = true) }

                loginWithKakaoUseCase(credential)
                    .onSuccess {
                        checkPendingConsent()
                    }.onFailure { throwable ->
                        when (throwable) {
                            is SocialLoginFailedException,
                            is InvalidCredentialException,
                            -> {
                                reduce { copy(isLoading = false) }
                                sendEffect(SplashEffect.ShowToast(throwable.message))
                            }

                            else -> {
                                handleBootstrapFailure(throwable)
                            }
                        }
                    }
            }
        }

        private suspend fun checkPendingConsent() {
            getPendingConsentListUseCase()
                .onSuccess { pending ->
                    when (pending.status) {
                        ConsentPendingStatus.NOT_SUBMITTED,
                        ConsentPendingStatus.STALE,
                        ConsentPendingStatus.UNKNOWN,
                        -> {
                            reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                            sendEffect(SplashEffect.RequireConsent)
                        }

                        ConsentPendingStatus.UP_TO_DATE -> {
                            checkUserProfile()
                        }
                    }
                }.onFailure { throwable ->
                    handleBootstrapFailure(throwable)
                }
        }

        private suspend fun checkUserProfile() {
            checkUserProfileUseCase()
                .onSuccess { profile ->
                    reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                    if (profile.name.isNullOrBlank()) {
                        // 이름이 비어 있으면 필수 프로필 미완성으로 보고 온보딩으로 보낸다.
                        sendEffect(SplashEffect.RequireOnboarding)
                    } else {
                        sendEffect(SplashEffect.Ready)
                    }
                }.onFailure { error ->
                    when (error) {
                        is UserNotFoundException -> {
                            reduce { copy(isLoading = false, showKakaoLoginButton = false) }
                            sendEffect(SplashEffect.RequireOnboarding)
                        }

                        else -> {
                            handleBootstrapFailure(error)
                        }
                    }
                }
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleBootstrapFailure(error: Throwable) {
            reduce { copy(isLoading = false) }
            when (error) {
                is NetworkUnavailableException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                }

                is ServerException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                }

                else -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
                }
            }
        }

        private companion object {
            const val DEFAULT_LOGIN_ERROR_MESSAGE = "로그인에 실패했습니다."
        }
    }

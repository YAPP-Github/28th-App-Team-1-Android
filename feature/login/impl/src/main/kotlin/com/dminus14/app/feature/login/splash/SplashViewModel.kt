package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.modal.GlobalModalRequest
import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.showGlobalModal
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetAuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getAuthSessionUseCase: GetAuthSessionUseCase,
        private val checkUserProfileUseCase: CheckUserProfileUseCase,
    ) : MviViewModel<SplashIntent, SplashState, SplashEffect>(SplashState) {
        override fun onIntent(intent: SplashIntent) {
            when (intent) {
                SplashIntent.Load -> load()
            }
        }

        private fun load() {
            viewModelScope.launch {
                getAuthSessionUseCase()
                    .onSuccess { session ->
                        if (session != null) {
                            checkUserProfile()
                        } else {
                            sendEffect(SplashEffect.SessionNotFound)
                        }
                    }.onFailure {
                        sendEffect(SplashEffect.SessionNotFound)
                    }
            }
        }

        private suspend fun checkUserProfile() {
            checkUserProfileUseCase()
                .onSuccess {
                    sendEffect(SplashEffect.SessionExists)
                }.onFailure {
                    when (it) {
                        is UserNotFoundException -> {
                            sendEffect(SplashEffect.SessionNotFound)
                        }

                        else -> {
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
    }

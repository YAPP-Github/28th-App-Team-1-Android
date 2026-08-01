package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
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
                }.onFailure { error ->
                    when (error) {
                        is UserNotFoundException -> {
                            sendEffect(SplashEffect.SessionNotFound)
                        }

                        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
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
        }
    }

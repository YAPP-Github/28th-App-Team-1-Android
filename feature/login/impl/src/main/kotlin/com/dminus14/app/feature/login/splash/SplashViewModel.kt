package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.usecase.GetAuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getAuthSessionUseCase: GetAuthSessionUseCase,
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
                            sendEffect(SplashEffect.SessionExists)
                        } else {
                            sendEffect(SplashEffect.SessionNotFound)
                        }
                    }.onFailure {
                        sendEffect(SplashEffect.SessionNotFound)
                    }
            }
        }
    }

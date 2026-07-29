package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor() : MviViewModel<SplashIntent, SplashState, SplashEffect>(SplashState()) {
        override fun onIntent(intent: SplashIntent) {
            when (intent) {
                SplashIntent.Load -> load()
            }
        }

        private fun load() {
            viewModelScope.launch {
                reduce { copy(isLoading = true) }
                // TODO: 스플래시 초기화 로직
                reduce { copy(isLoading = false) }
                sendEffect(SplashEffect.Finished)
            }
        }
    }

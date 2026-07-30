package com.dminus14.app.feature.login.splash

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
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
                    checkUserProfile()
                }.onFailure {
                    sendEffect(SplashEffect.SessionNotFound)
                }
        }
    }

    private fun checkUserProfile() {
        viewModelScope.launch {
            checkUserProfileUseCase()
                .onSuccess { profile ->
                    sendEffect(SplashEffect.SessionExists)
                }.onFailure {
                    sendEffect(SplashEffect.SessionNotFound)
                }
        }
    }
}

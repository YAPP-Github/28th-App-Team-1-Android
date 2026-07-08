package com.dminus14.app.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            LoginIntent.ClickKakaoLogin -> loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, errorMessage = null) }

            // TODO: Kakao SDK 연동 및 백엔드 토큰 교환
            reduce { copy(isLoading = false) }
            sendEffect(LoginEffect.NavigateToHome)
        }
    }

    private inline fun reduce(block: LoginState.() -> LoginState) {
        _state.update(block)
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

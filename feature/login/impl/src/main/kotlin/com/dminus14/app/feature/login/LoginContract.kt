package com.dminus14.app.feature.login

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface LoginIntent : MviIntent {
    /** 화면 진입 시 저장된 세션 존재 여부를 확인한다. */
    data object CheckSession : LoginIntent

    data object ClickKakaoLogin : LoginIntent

    data class KakaoLoginSucceeded(
        val credential: String,
    ) : LoginIntent

    data class KakaoLoginFailed(
        val error: Throwable,
    ) : LoginIntent
}

data class LoginState(
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : MviState

sealed interface LoginEffect : MviEffect {
    data object NavigateToHome : LoginEffect
}

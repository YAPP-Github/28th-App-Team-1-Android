package com.dminus14.app.feature.login

sealed interface LoginIntent {
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
)

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
}

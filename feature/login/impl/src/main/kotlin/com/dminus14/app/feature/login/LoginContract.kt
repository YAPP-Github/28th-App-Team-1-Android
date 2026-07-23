package com.dminus14.app.feature.login

sealed interface LoginIntent {
    data object ClickKakaoLogin : LoginIntent

    data class KakaoLoginSucceeded(
        val credential: String,
    ) : LoginIntent

    data class KakaoLoginFailed(
        val error: Throwable,
    ) : LoginIntent
}

data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
}

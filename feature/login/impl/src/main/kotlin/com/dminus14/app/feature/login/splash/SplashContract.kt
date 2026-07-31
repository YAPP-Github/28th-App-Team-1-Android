package com.dminus14.app.feature.login.splash

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface SplashIntent : MviIntent {
    /** 화면 진입 시 저장된 세션·프로필 존재 여부를 확인한다. */
    data object Load : SplashIntent

    data object ClickKakaoLogin : SplashIntent

    data class KakaoLoginSucceeded(
        val credential: String,
    ) : SplashIntent

    data class KakaoLoginFailed(
        val error: Throwable,
    ) : SplashIntent
}

data class SplashState(
    val showKakaoLoginButton: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : MviState

sealed interface SplashEffect : MviEffect {
    /** 유효한 세션과 프로필이 모두 존재한다. */
    data object ProfileExists : SplashEffect

    /** 세션은 있으나 프로필이 없다. */
    data object ProfileNotFound : SplashEffect
}

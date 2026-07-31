package com.dminus14.app.feature.login.splash

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface SplashIntent : MviIntent {
    /** 화면 진입 시 저장된 세션 존재 여부를 확인한다. */
    data object Load : SplashIntent
}

data object SplashState : MviState

sealed interface SplashEffect : MviEffect {
    /** 유효한 인증 세션이 존재한다. */
    data object SessionExists : SplashEffect

    /** 인증 세션이 없다. */
    data object SessionNotFound : SplashEffect

    data object UnknownError : SplashEffect
}

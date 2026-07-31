package com.dminus14.app.feature.login.splash

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface SplashIntent : MviIntent {
    data object Load : SplashIntent
}

data class SplashState(
    val isLoading: Boolean = true,
) : MviState

sealed interface SplashEffect : MviEffect {
    data object Finished : SplashEffect
}

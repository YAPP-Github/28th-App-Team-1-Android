package com.dminus14.app.feature.login.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface OnboardingIntent : MviIntent {
    data object Load : OnboardingIntent

    data object ClickComplete : OnboardingIntent
}

data class OnboardingState(
    val isLoading: Boolean = false,
) : MviState

sealed interface OnboardingEffect : MviEffect {
    data object Completed : OnboardingEffect
}

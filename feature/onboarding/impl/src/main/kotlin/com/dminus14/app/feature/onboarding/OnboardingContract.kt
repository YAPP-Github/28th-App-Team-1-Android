package com.dminus14.app.feature.onboarding

sealed interface OnboardingIntent {
    data object Load : OnboardingIntent
}

data class OnboardingState(
    val title: String = "",
    val isLoading: Boolean = false,
)

sealed interface OnboardingEffect

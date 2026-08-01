package com.dminus14.app.feature.feedback.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface FeedbackOnboardingIntent : MviIntent {
    data class Load(
        val token: String,
    ) : FeedbackOnboardingIntent

    data object StartClicked : FeedbackOnboardingIntent

    data class NicknameChanged(
        val value: String,
    ) : FeedbackOnboardingIntent

    data object NicknameConfirmed : FeedbackOnboardingIntent

    data object NameEditorDismissed : FeedbackOnboardingIntent

    data object BackPressed : FeedbackOnboardingIntent
}

data class FeedbackOnboardingState(
    val loadState: FeedbackOnboardingLoadState = FeedbackOnboardingLoadState.Idle,
    val requesterName: String = "",
    val nickname: String = "",
    val isNameEditorVisible: Boolean = false,
) : MviState {
    val canContinue: Boolean
        get() {
            val normalized = nickname.trim()
            return normalized.isNotEmpty() &&
                normalized.length <= 12 &&
                '\n' !in normalized &&
                '\r' !in normalized
        }
}

enum class FeedbackOnboardingLoadState {
    Idle,
    Loading,
    Ready,
    Failed,
}

sealed interface FeedbackOnboardingEffect : MviEffect {
    data object FeedbackReady : FeedbackOnboardingEffect

    data object ExitRequested : FeedbackOnboardingEffect

    data object ShowExitHint : FeedbackOnboardingEffect
}

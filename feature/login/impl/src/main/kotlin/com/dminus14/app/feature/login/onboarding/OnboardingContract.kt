package com.dminus14.app.feature.login.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface OnboardingIntent : MviIntent {
    data object Load : OnboardingIntent

    data object CloseClick : OnboardingIntent

    data object ContinueClick : OnboardingIntent

    data object PreviousClick : OnboardingIntent

    data class NameChange(
        val value: String,
    ) : OnboardingIntent

    data class JobClick(
        val index: Int,
    ) : OnboardingIntent

    data class ExperienceChange(
        val index: Int,
    ) : OnboardingIntent
}

enum class OnboardingStep {
    Naming,
    JobSelection,
    ExperienceSelection,
    RegisterDone,
}

data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.Naming,
    val name: String = "",
    val jobs: List<String> = DefaultJobs,
    val selectedJobIndex: Int? = null,
    val experienceOptions: List<String> = DefaultExperienceOptions,
    val selectedExperienceIndex: Int = 0,
    val isContinueEnabled: Boolean = false,
) : MviState

sealed interface OnboardingEffect : MviEffect {
    data object CloseRequested : OnboardingEffect

    data object Completed : OnboardingEffect
}

internal val DefaultJobs =
    listOf(
        "백엔드",
        "데이터 엔지니어",
        "Android",
        "iOS",
        "프론트엔드",
        "인프라 · SRE",
    )

internal val DefaultExperienceOptions =
    listOf(
        "경력 없음",
        "신입",
        "1년 이상",
        "2년 이상",
        "3년 이상",
        "4년 이상",
        "5년 이상",
        "6년 이상",
        "7년 이상",
        "8년 이상",
        "9년 이상",
        "10년 이상",
    )

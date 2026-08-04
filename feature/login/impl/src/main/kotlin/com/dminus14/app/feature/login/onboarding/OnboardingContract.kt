package com.dminus14.app.feature.login.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.domain.model.Job

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
    val jobs: List<Job> = emptyList(),
    val selectedJobIndex: Int? = null,
    val experienceOptions: List<String> = DefaultExperienceOptions,
    val selectedExperienceIndex: Int = 0,
    val isContinueEnabled: Boolean = false,
    /** 프로필 등록 API 진행 중 여부. 중복 클릭 방지에 사용한다. */
    val isSubmitting: Boolean = false,
    /** InvalidJobRole·NameAlreadyTaken·Validation 등 인라인으로 보여줄 사용자 메시지. */
    val errorMessage: String? = null,
) : MviState

sealed interface OnboardingEffect : MviEffect {
    data object CloseRequested : OnboardingEffect

    /** 프로필 등록 성공. 화면에서 Home 으로 이동한다. */
    data object Completed : OnboardingEffect
}

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

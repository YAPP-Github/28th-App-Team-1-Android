package com.dminus14.app.feature.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface OnBoardingInterviewIntent : MviIntent {
    data object Load : OnBoardingInterviewIntent

    data object ClickClose : OnBoardingInterviewIntent

    data object ClickSkip : OnBoardingInterviewIntent

    data object ClickContinue : OnBoardingInterviewIntent

    data object ClickPrevious : OnBoardingInterviewIntent

    data class JobDescriptionTabChange(
        val index: Int,
    ) : OnBoardingInterviewIntent

    data class JobDescriptionLinkChange(
        val value: String,
    ) : OnBoardingInterviewIntent

    data class JobDescriptionTextChange(
        val value: String,
    ) : OnBoardingInterviewIntent

    data object ClickPortfolioUpload : OnBoardingInterviewIntent

    data object ClickPortfolioRemove : OnBoardingInterviewIntent

    data object ClickPortfolioUseExisting : OnBoardingInterviewIntent

    data object ClickPortfolioUploadNew : OnBoardingInterviewIntent

    data class MainProjectTextChange(
        val value: String,
    ) : OnBoardingInterviewIntent
}

enum class OnBoardingInterviewStep {
    JobDescription,
    Portfolio,
    MainProject,
    Preload,
}

enum class JobDescriptionTab {
    Link,
    Text,
}

/** [OnBoardingLoadingStepRow]의 진행 상태. */
enum class OnBoardingLoadingStepStatus {
    Waiting,
    InProgress,
    Completed,
}

data class OnBoardingInterviewState(
    val step: OnBoardingInterviewStep = OnBoardingInterviewStep.JobDescription,
    val jobDescriptionTab: JobDescriptionTab = JobDescriptionTab.Link,
    val jobDescriptionLink: String = "",
    val jobDescriptionText: String = "",
    val portfolioFileName: String? = null,
    val showExistingPortfolioModal: Boolean = false,
    val showPortfolioRequiredError: Boolean = false,
    val mainProjectText: String = "",
) : MviState

sealed interface OnBoardingInterviewEffect : MviEffect {
    data object CloseRequested : OnBoardingInterviewEffect
}

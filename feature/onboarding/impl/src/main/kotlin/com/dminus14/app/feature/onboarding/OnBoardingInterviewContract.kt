package com.dminus14.app.feature.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import java.io.File

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

    /** SAF 파일 선택기가 PDF를 캐시로 복사해 돌려준 결과. */
    data class PortfolioFileSelected(
        val file: File,
        val fileName: String,
    ) : OnBoardingInterviewIntent

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
    val isPortfolioProcessing: Boolean = false,
    val mainProjectText: String = "",
    val isBusy: Boolean = false,
    val errorMessage: String? = null,
    val loadingBasicInfo: OnBoardingLoadingStepStatus = OnBoardingLoadingStepStatus.Waiting,
    val loadingJd: OnBoardingLoadingStepStatus = OnBoardingLoadingStepStatus.Waiting,
    val loadingPortfolio: OnBoardingLoadingStepStatus = OnBoardingLoadingStepStatus.Waiting,
) : MviState

sealed interface OnBoardingInterviewEffect : MviEffect {
    data object CloseRequested : OnBoardingInterviewEffect

    /** 포트폴리오 PDF를 고르기 위해 SAF 문서 선택기를 연다. */
    data object LaunchPortfolioPicker : OnBoardingInterviewEffect

    /** 면접 세션이 준비(READY)되어 결과 화면으로 이동한다. */
    data class NavigateToResult(
        val sessionId: Long,
    ) : OnBoardingInterviewEffect
}

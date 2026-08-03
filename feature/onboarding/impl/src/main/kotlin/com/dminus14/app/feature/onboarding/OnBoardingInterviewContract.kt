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

/** JD 링크 입력 필드의 비동기 검증 상태. */
enum class JdLinkStatus {
    /** 입력 전(비어 있음) 또는 스킴을 아직 입력 중. */
    Idle,

    /** URL 검증 API 호출 중. */
    Validating,

    /** 검증 성공. */
    Valid,

    /** 포맷 오류 또는 검증 실패. */
    Invalid,
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
    val jdLinkStatus: JdLinkStatus = JdLinkStatus.Idle,
    val jdLinkSubText: String = "",
    val jobDescriptionText: String = "",
    /** 직접 입력(Text) 탭의 검증 에러 메시지. 표기 방식은 디자이너 협의 예정이라 저장만 한다. */
    val jdTextError: String? = null,
    val portfolioFileName: String? = null,
    val showExistingPortfolioModal: Boolean = false,
    /** 포트폴리오 스텝 인라인 에러(필수 누락·PDF 검증 실패 등). null이면 숨긴다. */
    val portfolioErrorMessage: String? = null,
    val isPortfolioProcessing: Boolean = false,
    val mainProjectText: String = "",
    /** 집중 프로젝트 입력의 검증 에러 메시지. 표기 방식은 디자이너 협의 예정이라 저장만 한다. */
    val mainProjectError: String? = null,
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

package com.dminus14.app.feature.onboarding

/** [OnBoardingInterviewViewModel]의 [JD_TEXT_MIN_LENGTH]와 동일. */
internal const val JOB_DESCRIPTION_TEXT_MIN_LENGTH = 200

internal fun OnBoardingInterviewState.isBottomBarEnabled(): Boolean =
    existingPortfolioModalPhase == ExistingPortfolioModalPhase.None

/**
 * 계속하기 버튼 활성 여부를 스텝별로 파생한다.
 * - JobDescription: 링크 탭은 빈 입력(건너뛰기) 또는 검증 성공, 직접 입력 탭은 200자 이상일 때만 활성.
 * - Portfolio: 업로드가 끝나 완료 카드(`PdfUploadType.Completed`)로 표시되는 상태와 같은 조건
 *   (`!isProcessing && fileName != null`)에서만 활성.
 * - 나머지 스텝: 각 스텝 검증은 [OnBoardingInterviewViewModel.onIntent] Continue 처리에서 담당.
 */
internal fun OnBoardingInterviewState.isContinueEnabled(): Boolean =
    when (step) {
        OnBoardingInterviewStep.JobDescription -> {
            isJobDescriptionContinueEnabled()
        }

        OnBoardingInterviewStep.Portfolio -> {
            !isPortfolioProcessing && portfolioFileName != null
        }

        OnBoardingInterviewStep.MainProject,
        OnBoardingInterviewStep.Preload,
        -> {
            true
        }
    }

/** [OnBoardingInterviewViewModel.submitJobDescription]과 동일한 진행 가능 조건. */
private fun OnBoardingInterviewState.isJobDescriptionContinueEnabled(): Boolean =
    when (jobDescriptionTab) {
        JobDescriptionTab.Link -> {
            jobDescriptionLink.isEmpty() || jdLinkStatus == JdLinkStatus.Valid
        }

        JobDescriptionTab.Text -> {
            jobDescriptionText.length >= JOB_DESCRIPTION_TEXT_MIN_LENGTH
        }
    }

internal fun OnBoardingInterviewStep.toProgressStep(): Int =
    when (this) {
        OnBoardingInterviewStep.JobDescription -> 1

        OnBoardingInterviewStep.Portfolio -> 2

        OnBoardingInterviewStep.MainProject,
        OnBoardingInterviewStep.Preload,
        -> PROGRESS_MAX_STEP
    }

internal const val PROGRESS_MAX_STEP = 3

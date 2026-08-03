package com.dminus14.app.feature.onboarding

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingInterviewViewModel
    @Inject
    constructor() :
    MviViewModel<OnBoardingInterviewIntent, OnBoardingInterviewState, OnBoardingInterviewEffect>(
            OnBoardingInterviewState(),
        ) {
        override fun onIntent(intent: OnBoardingInterviewIntent) {
            when (intent) {
                OnBoardingInterviewIntent.Load -> {
                    Unit
                }

                OnBoardingInterviewIntent.ClickClose -> {
                    sendEffect(OnBoardingInterviewEffect.CloseRequested)
                }

                OnBoardingInterviewIntent.ClickSkip,
                OnBoardingInterviewIntent.ClickContinue,
                -> {
                    onContinueClick()
                }

                OnBoardingInterviewIntent.ClickPrevious -> {
                    onPreviousClick()
                }

                is OnBoardingInterviewIntent.JobDescriptionTabChange -> {
                    reduce { copy(jobDescriptionTab = JobDescriptionTab.entries[intent.index]) }
                }

                is OnBoardingInterviewIntent.JobDescriptionLinkChange -> {
                    reduce { copy(jobDescriptionLink = intent.value) }
                }

                is OnBoardingInterviewIntent.JobDescriptionTextChange -> {
                    reduce { copy(jobDescriptionText = intent.value) }
                }

                OnBoardingInterviewIntent.ClickPortfolioUpload -> {
                    reduce { copy(showExistingPortfolioModal = true) }
                }

                OnBoardingInterviewIntent.ClickPortfolioRemove -> {
                    reduce { copy(portfolioFileName = null) }
                }

                OnBoardingInterviewIntent.ClickPortfolioUseExisting -> {
                    reduce {
                        copy(
                            portfolioFileName = EXISTING_PORTFOLIO_FILE_NAME,
                            showExistingPortfolioModal = false,
                            showPortfolioRequiredError = false,
                        )
                    }
                }

                OnBoardingInterviewIntent.ClickPortfolioUploadNew -> {
                    reduce {
                        copy(
                            portfolioFileName = NEW_PORTFOLIO_FILE_NAME,
                            showExistingPortfolioModal = false,
                            showPortfolioRequiredError = false,
                        )
                    }
                }

                is OnBoardingInterviewIntent.MainProjectTextChange -> {
                    reduce { copy(mainProjectText = intent.value) }
                }
            }
        }

        private fun onContinueClick() {
            val current = state.value
            if (current.step == OnBoardingInterviewStep.Portfolio &&
                current.portfolioFileName == null
            ) {
                reduce { copy(showPortfolioRequiredError = true) }
                return
            }

            val nextStep =
                when (current.step) {
                    OnBoardingInterviewStep.JobDescription -> OnBoardingInterviewStep.Portfolio
                    OnBoardingInterviewStep.Portfolio -> OnBoardingInterviewStep.MainProject
                    OnBoardingInterviewStep.MainProject -> OnBoardingInterviewStep.Preload
                    OnBoardingInterviewStep.Preload -> return
                }
            reduce { copy(step = nextStep) }
        }

        private fun onPreviousClick() {
            val previousStep =
                when (state.value.step) {
                    OnBoardingInterviewStep.JobDescription -> {
                        sendEffect(OnBoardingInterviewEffect.CloseRequested)
                        return
                    }

                    OnBoardingInterviewStep.Portfolio -> {
                        OnBoardingInterviewStep.JobDescription
                    }

                    OnBoardingInterviewStep.MainProject -> {
                        OnBoardingInterviewStep.Portfolio
                    }

                    OnBoardingInterviewStep.Preload -> {
                        OnBoardingInterviewStep.MainProject
                    }
                }
            reduce { copy(step = previousStep) }
        }

        private companion object {
            const val EXISTING_PORTFOLIO_FILE_NAME = "기존_포트폴리오.pdf"
            const val NEW_PORTFOLIO_FILE_NAME = "포트폴리오.pdf"
        }
    }

package com.dminus14.app.feature.login.onboarding

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor() :
    MviViewModel<OnboardingIntent, OnboardingState, OnboardingEffect>(OnboardingState()) {
        override fun onIntent(intent: OnboardingIntent) {
            when (intent) {
                OnboardingIntent.Load -> {
                    reduce {
                        copy(
                            jobs = DefaultJobs,
                            experienceOptions = DefaultExperienceOptions,
                            selectedExperienceIndex = 0,
                            isContinueEnabled = resolveContinueEnabled(this),
                        )
                    }
                }

                OnboardingIntent.CloseClick -> {
                    sendEffect(OnboardingEffect.CloseRequested)
                }

                OnboardingIntent.ContinueClick -> onContinueClick()

                OnboardingIntent.PreviousClick -> onPreviousClick()

                is OnboardingIntent.NameChange -> {
                    reduce {
                        copy(name = sanitizeNickname(intent.value)).withContinueEnabled()
                    }
                }

                is OnboardingIntent.JobClick -> {
                    reduce {
                        copy(selectedJobIndex = intent.index).withContinueEnabled()
                    }
                }

                is OnboardingIntent.ExperienceChange -> {
                    reduce {
                        copy(selectedExperienceIndex = intent.index).withContinueEnabled()
                    }
                }
            }
        }

        private fun onContinueClick() {
            val current = state.value
            if (current.step != OnboardingStep.RegisterDone && !current.isContinueEnabled) {
                return
            }

            when (current.step) {
                OnboardingStep.Naming -> {
                    reduce {
                        copy(step = OnboardingStep.JobSelection).withContinueEnabled()
                    }
                }

                OnboardingStep.JobSelection -> {
                    reduce {
                        copy(step = OnboardingStep.ExperienceSelection).withContinueEnabled()
                    }
                }

                OnboardingStep.ExperienceSelection -> {
                    reduce {
                        copy(step = OnboardingStep.RegisterDone, isContinueEnabled = true)
                    }
                }

                OnboardingStep.RegisterDone -> {
                    sendEffect(OnboardingEffect.Completed)
                }
            }
        }

        private fun onPreviousClick() {
            when (state.value.step) {
                OnboardingStep.Naming -> {
                    sendEffect(OnboardingEffect.CloseRequested)
                }

                OnboardingStep.JobSelection -> {
                    reduce {
                        copy(step = OnboardingStep.Naming).withContinueEnabled()
                    }
                }

                OnboardingStep.ExperienceSelection -> {
                    reduce {
                        copy(step = OnboardingStep.JobSelection).withContinueEnabled()
                    }
                }

                OnboardingStep.RegisterDone -> {
                    reduce {
                        copy(step = OnboardingStep.ExperienceSelection).withContinueEnabled()
                    }
                }
            }
        }

        private fun OnboardingState.withContinueEnabled(): OnboardingState =
            copy(isContinueEnabled = resolveContinueEnabled(this))

        private fun resolveContinueEnabled(state: OnboardingState): Boolean =
            when (state.step) {
                OnboardingStep.Naming -> isValidNickname(state.name)
                OnboardingStep.JobSelection -> state.selectedJobIndex != null
                OnboardingStep.ExperienceSelection ->
                    state.selectedExperienceIndex in state.experienceOptions.indices
                OnboardingStep.RegisterDone -> true
            }

        private fun sanitizeNickname(input: String): String =
            input
                .filter { char -> NICKNAME_ALLOWED_CHAR_REGEX.matches(char.toString()) }
                .take(NICKNAME_MAX_LENGTH)

        private fun isValidNickname(name: String): Boolean =
            sanitizeNickname(name) == name && name.isNotBlank()

        private companion object {
            private const val NICKNAME_MAX_LENGTH = 5
            private val NICKNAME_ALLOWED_CHAR_REGEX = Regex("[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]")
        }
    }

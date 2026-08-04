package com.dminus14.app.feature.login.onboarding

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.InvalidJobRoleException
import com.dminus14.app.domain.exception.NameAlreadyTakenException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.UserProfileUpdate
import com.dminus14.app.domain.usecase.GetJobListUseCase
import com.dminus14.app.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val getJobList: GetJobListUseCase,
        private val updateUserProfile: UpdateUserProfileUseCase,
    ) : MviViewModel<OnboardingIntent, OnboardingState, OnboardingEffect>(OnboardingState()) {
        override fun onIntent(intent: OnboardingIntent) {
            when (intent) {
                OnboardingIntent.Load -> {
                    reduce {
                        copy(
                            experienceOptions = DefaultExperienceOptions,
                            selectedExperienceIndex = 0,
                            isContinueEnabled = resolveContinueEnabled(this),
                        )
                    }
                    loadJobs()
                }

                OnboardingIntent.CloseClick -> {
                    sendEffect(OnboardingEffect.CloseRequested)
                }

                OnboardingIntent.ContinueClick -> {
                    onContinueClick()
                }

                OnboardingIntent.PreviousClick -> {
                    onPreviousClick()
                }

                is OnboardingIntent.NameChange -> {
                    reduce {
                        copy(
                            name = sanitizeNickname(intent.value),
                            errorMessage = null,
                        ).withContinueEnabled()
                    }
                }

                is OnboardingIntent.JobClick -> {
                    reduce {
                        copy(
                            selectedJobIndex = intent.index,
                            errorMessage = null,
                        ).withContinueEnabled()
                    }
                }

                is OnboardingIntent.ExperienceChange -> {
                    reduce {
                        copy(selectedExperienceIndex = intent.index).withContinueEnabled()
                    }
                }
            }
        }

        private fun loadJobs() {
            viewModelScope.launch {
                getJobList()
                    .onSuccess { jobs ->
                        reduce { copy(jobs = jobs).withContinueEnabled() }
                    }.onFailure { error ->
                        handleCommonError(error)
                    }
            }
        }

        private fun onContinueClick() {
            val current = state.value
            if (current.step != OnboardingStep.RegisterDone && !current.isContinueEnabled) {
                return
            }
            if (current.isSubmitting) {
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
                    submitProfile()
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

        private fun submitProfile() {
            val current = state.value
            val selectedJob =
                current.selectedJobIndex?.let { current.jobs.getOrNull(it) } ?: return
            val careerYears = current.selectedExperienceIndex

            reduce { copy(isSubmitting = true, errorMessage = null) }
            viewModelScope.launch {
                updateUserProfile(
                    UserProfileUpdate(
                        name = current.name,
                        jobRole = selectedJob.jobRole,
                        careerYears = careerYears,
                    ),
                ).onSuccess {
                    reduce { copy(isSubmitting = false) }
                    sendEffect(OnboardingEffect.Completed)
                }.onFailure { error ->
                    when (error) {
                        is NameAlreadyTakenException -> {
                            reduce {
                                copy(
                                    step = OnboardingStep.Naming,
                                    isSubmitting = false,
                                    errorMessage = error.message,
                                ).withContinueEnabled()
                            }
                        }

                        is InvalidJobRoleException -> {
                            reduce {
                                copy(
                                    step = OnboardingStep.JobSelection,
                                    isSubmitting = false,
                                    errorMessage = error.message,
                                ).withContinueEnabled()
                            }
                        }

                        is ValidationException -> {
                            reduce {
                                copy(isSubmitting = false, errorMessage = error.message)
                            }
                        }

                        else -> {
                            handleCommonError(error)
                        }
                    }
                }
            }
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleCommonError(error: Throwable) {
            reduce { copy(isSubmitting = false) }
            when (error) {
                is NetworkUnavailableException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                }

                is ServerException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                }

                else -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
                }
            }
        }

        private fun OnboardingState.withContinueEnabled(): OnboardingState =
            copy(isContinueEnabled = resolveContinueEnabled(this))

        private fun resolveContinueEnabled(state: OnboardingState): Boolean =
            when (state.step) {
                OnboardingStep.Naming -> {
                    isValidNickname(state.name)
                }

                OnboardingStep.JobSelection -> {
                    state.selectedJobIndex != null &&
                        state.selectedJobIndex in state.jobs.indices
                }

                OnboardingStep.ExperienceSelection -> {
                    state.selectedExperienceIndex in state.experienceOptions.indices
                }

                OnboardingStep.RegisterDone -> {
                    true
                }
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

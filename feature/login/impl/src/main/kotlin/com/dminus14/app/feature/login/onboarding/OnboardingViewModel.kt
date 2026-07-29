package com.dminus14.app.feature.login.onboarding

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor() : MviViewModel<OnboardingIntent, OnboardingState, OnboardingEffect>(OnboardingState()) {
        override fun onIntent(intent: OnboardingIntent) {
            when (intent) {
                OnboardingIntent.Load -> {
                    reduce { copy(isLoading = false) }
                }

                OnboardingIntent.ClickComplete -> {
                    sendEffect(OnboardingEffect.Completed)
                }
            }
        }
    }

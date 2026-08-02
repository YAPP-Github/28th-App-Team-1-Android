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

                OnBoardingInterviewIntent.CloseClick -> {
                    sendEffect(OnBoardingInterviewEffect.CloseRequested)
                }
            }
        }
    }

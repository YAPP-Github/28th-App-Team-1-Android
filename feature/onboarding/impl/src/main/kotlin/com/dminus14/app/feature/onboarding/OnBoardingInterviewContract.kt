package com.dminus14.app.feature.onboarding

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface OnBoardingInterviewIntent : MviIntent {
    data object Load : OnBoardingInterviewIntent

    data object CloseClick : OnBoardingInterviewIntent
}

data class OnBoardingInterviewState(
    val isLoading: Boolean = false,
) : MviState

sealed interface OnBoardingInterviewEffect : MviEffect {
    data object CloseRequested : OnBoardingInterviewEffect
}

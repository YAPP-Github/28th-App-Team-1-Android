package com.dminus14.app.feature.interview.error

import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.feature.interview.api.InterviewErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InterviewErrorViewModel
    @Inject
    constructor() :
    MviViewModel<InterviewErrorIntent, InterviewErrorState, InterviewErrorEffect>(
            InterviewErrorState(),
        ) {
        override fun onIntent(intent: InterviewErrorIntent) {
            when (intent) {
                InterviewErrorIntent.ClickAbort -> sendEffect(InterviewErrorEffect.NavigateToHome)
                InterviewErrorIntent.ClickResume -> sendEffect(InterviewErrorEffect.ResumeInterview)
            }
        }

        fun initErrorType(errorType: InterviewErrorType) {
            reduce { copy(errorType = errorType) }
        }
    }

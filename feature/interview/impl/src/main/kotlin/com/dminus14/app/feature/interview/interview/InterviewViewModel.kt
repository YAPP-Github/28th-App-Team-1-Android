package com.dminus14.app.feature.interview.interview

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel
    @Inject
    constructor() :
    MviViewModel<InterviewIntent, InterviewState, InterviewEffect>(InterviewState()) {
        @Suppress("CyclomaticComplexMethod")
        override fun onIntent(intent: InterviewIntent) {
            when (intent) {
                InterviewIntent.StartInterview -> {
                    reduce {
                        copy(
                            interviewScreenState = InterviewScreenState.ONGOING,
                        )
                    }
                }

                InterviewIntent.ChangeSpeaker -> {
                    reduce {
                        copy(
                            speaker = !speaker,
                        )
                    }
                }
            }
        }
    }

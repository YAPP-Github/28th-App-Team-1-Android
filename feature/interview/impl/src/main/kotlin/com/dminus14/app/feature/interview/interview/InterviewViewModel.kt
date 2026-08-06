package com.dminus14.app.feature.interview.interview

import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.core.permission.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TOAST_8MIN_DURATION_MS = 5000L
private const val TOAST_12MIN_DURATION_MS = 3000L

@HiltViewModel
class InterviewViewModel
    @Inject
    constructor(
        private val permissionManager: PermissionManager,
    ) : MviViewModel<InterviewIntent, InterviewState, InterviewEffect>(InterviewState()) {
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

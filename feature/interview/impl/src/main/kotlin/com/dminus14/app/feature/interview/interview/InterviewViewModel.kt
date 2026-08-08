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
                InterviewIntent.CheckCameraPermission -> {
                    // 후속 구현: 기존 권한 처리 계층에서 CAMERA 권한 상태를 조회해 State에 반영한다.
                }

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

                InterviewIntent.ReportCameraBindingFailure -> {
                    sendEffect(InterviewEffect.CameraBindingFailed)
                }

                InterviewIntent.ClickPermissionDeniedBack -> {
                    sendEffect(InterviewEffect.PermissionDeniedExitRequested)
                }

                InterviewIntent.ClickFinishInterview -> {
                    sendEffect(InterviewEffect.FinishRequested)
                }
            }
        }
    }

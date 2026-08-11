package com.dminus14.app.feature.interviewreport

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 리포트 화면 ViewModel skeleton.
 *
 * MVI 배선과 UseCase 주입은 C4 커밋에서 채운다. 지금은 컴파일 통과와 Screen에서
 * hiltViewModel() 로 얻기 위한 최소 형태만 유지한다.
 */
@HiltViewModel
class InterviewReportViewModel
    @Inject
    constructor() :
    MviViewModel<InterviewReportIntent, InterviewReportState, InterviewReportEffect>(
        InterviewReportState(),
    ) {
        override fun onIntent(intent: InterviewReportIntent) {
            // TODO(C4): UseCase 배선 및 Intent 처리.
        }
    }

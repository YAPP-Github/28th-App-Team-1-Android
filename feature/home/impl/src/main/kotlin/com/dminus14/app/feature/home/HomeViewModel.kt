package com.dminus14.app.feature.home

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor() :
    MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
        override fun onIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.Load -> {
                    reduce {
                        copy(
                            isLoading = false,
                            userName = "재원",
                            reports = PreviewHomeReports,
                            expandedReportIds = setOfNotNull(PreviewHomeReports.firstOrNull()?.id),
                        )
                    }
                }

                HomeIntent.OpenMyPage -> {
                    sendEffect(HomeEffect.GoToMyPageRequested)
                }

                is HomeIntent.ReportExpandClick -> {
                    reduce {
                        copy(
                            expandedReportIds =
                                if (intent.reportId in expandedReportIds) {
                                    expandedReportIds - intent.reportId
                                } else {
                                    expandedReportIds + intent.reportId
                                },
                        )
                    }
                }

                is HomeIntent.ReportActionClick -> {
                    Unit
                }
            }
        }
    }

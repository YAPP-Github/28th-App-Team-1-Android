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
                            reports = emptyList(),
                        )
                    }
                }

                is HomeIntent.ReportClick -> Unit
            }
        }
    }

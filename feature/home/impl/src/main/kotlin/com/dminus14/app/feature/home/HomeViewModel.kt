package com.dminus14.app.feature.home

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor() :
    MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
        override fun onIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.Load -> load()
                HomeIntent.OpenMyPage -> sendEffect(HomeEffect.GoToMyPageRequested)
            }
        }

        private fun load() {
            viewModelScope.launch {
                reduce { copy(isLoading = true) }
                reduce {
                    copy(
                        isLoading = false,
                        title = "Home",
                    )
                }
            }
        }
    }

package com.dminus14.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(OnboardingState())
        val state: StateFlow<OnboardingState> = _state.asStateFlow()

        fun onIntent(intent: OnboardingIntent) {
            when (intent) {
                OnboardingIntent.Load -> load()
            }
        }

        private fun load() {
            viewModelScope.launch {
                reduce { copy(isLoading = true) }
                reduce {
                    copy(
                        isLoading = false,
                        title = "Onboarding",
                    )
                }
            }
        }

        private inline fun reduce(block: OnboardingState.() -> OnboardingState) {
            _state.update(block)
        }
    }

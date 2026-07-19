package com.dminus14.app.feature.main

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
class MainViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(MainState())
        val state: StateFlow<MainState> = _state.asStateFlow()

        fun onIntent(intent: MainIntent) {
            when (intent) {
                MainIntent.Load -> load()
            }
        }

        private fun load() {
            viewModelScope.launch {
                reduce { copy(isLoading = true) }
                reduce {
                    copy(
                        isLoading = false,
                        title = "Main Home",
                    )
                }
            }
        }

        private inline fun reduce(block: MainState.() -> MainState) {
            _state.update(block)
        }
    }

package com.dminus14.app.feature.main

sealed interface MainIntent {
    data object Load : MainIntent
}

data class MainState(
    val title: String = "",
    val isLoading: Boolean = false,
)

sealed interface MainEffect

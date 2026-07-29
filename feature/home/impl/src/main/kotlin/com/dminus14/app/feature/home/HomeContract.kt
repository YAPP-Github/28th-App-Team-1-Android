package com.dminus14.app.feature.home

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface HomeIntent : MviIntent {
    data object Load : HomeIntent
}

data class HomeState(
    val isLoading: Boolean = false,
    val title: String = "",
) : MviState

sealed interface HomeEffect : MviEffect

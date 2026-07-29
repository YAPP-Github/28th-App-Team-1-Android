package com.dminus14.app.feature.login.term

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface TermIntent : MviIntent {
    data object Load : TermIntent

    data object ClickAgree : TermIntent
}

data class TermState(
    val isLoading: Boolean = false,
) : MviState

sealed interface TermEffect : MviEffect {
    data object Agreed : TermEffect
}

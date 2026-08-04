package com.dminus14.app.feature.login.term

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface TermIntent : MviIntent {
    data object Load : TermIntent

    data object ClickClose : TermIntent

    data object ClickAllAgree : TermIntent

    data class ClickTerm(
        val index: Int,
    ) : TermIntent

    data class ClickViewTerm(
        val index: Int,
    ) : TermIntent

    data object DismissTermDetail : TermIntent

    data object ClickAgree : TermIntent
}

data class TermDetailContent(
    val title: String,
    val body: String,
    val isEssential: Boolean,
    val isChecked: Boolean = false,
) {
    fun hasContent(): Boolean = body.isNotBlank()
}

data class TermState(
    val terms: List<TermDetailContent> = emptyList(),
    val visibleTermDetailIndex: Int? = null,
    val isLoading: Boolean = false,
) : MviState {
    val isAllChecked: Boolean
        get() = terms.isNotEmpty() && terms.all(TermDetailContent::isChecked)

    val isEssentialAllChecked: Boolean
        get() =
            terms.filter(TermDetailContent::isEssential).let { essentials ->
                essentials.isNotEmpty() && essentials.all(TermDetailContent::isChecked)
            }

    val canSubmit: Boolean
        get() = isEssentialAllChecked && !isLoading

    val visibleTermDetail: TermDetailContent?
        get() = visibleTermDetailIndex?.let(terms::getOrNull)
}

sealed interface TermEffect : MviEffect {
    data object Agreed : TermEffect

    data object Closed : TermEffect

    data object GrantPerm : TermEffect

    data object DeniedPerm : TermEffect
}

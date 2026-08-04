package com.dminus14.app.feature.login.term

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.domain.model.ConsentItem

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

data class TermState(
    val terms: List<ConsentItem> = emptyList(),
    val visibleTermDetailIndex: Int? = null,
    val isLoading: Boolean = false,
    /** 로드/문서 조회 실패 시 사용자에게 보여줄 메시지. 표기 위치는 디자이너 협의 예정. */
    val errorMessage: String? = null,
    val visibleTermDetail: TermDetailContent? = null,
) : MviState {
    val isAllChecked: Boolean
        get() = terms.isNotEmpty() && terms.all(ConsentItem::isChecked)

    val isEssentialAllChecked: Boolean
        get() =
            terms.filter(ConsentItem::isRequired).let { essentials ->
                essentials.isNotEmpty() && essentials.all(ConsentItem::isChecked)
            }

    val canSubmit: Boolean
        get() = isEssentialAllChecked && !isLoading

}

data class TermDetailContent(
    val title: String,
    val content: String,
)

sealed interface TermEffect : MviEffect {
    data object Agreed : TermEffect

    data object Closed : TermEffect

    data object GrantPerm : TermEffect

    data object DeniedPerm : TermEffect

    /** 제출 실패 등 사용자에게 안내할 1회성 Toast를 표시한다. */
    data class ShowToast(
        val message: String,
    ) : TermEffect
}

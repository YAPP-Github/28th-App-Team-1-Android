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
    /** pending으로 내려온 동의 항목 목록. 각 항목의 체크 상태를 포함한다. */
    val terms: List<ConsentItem> = emptyList(),
    /** 서버 통신(목록 조회·문서 조회·제출) 진행 여부. */
    val isLoading: Boolean = false,
    /** 열려 있는 약관 상세 시트 내용. null이면 시트를 닫는다. */
    val visibleTermDetail: TermDetailContent? = null,
) : MviState {
    /** 필수·선택 항목 전부 체크됐는지. 전체 동의 체크박스 상태에 사용한다. */
    val isAllChecked: Boolean
        get() = terms.isNotEmpty() && terms.all(ConsentItem::isChecked)

    /** 필수 항목이 모두 체크됐는지. 제출 가능 여부의 기준이다. */
    val isEssentialAllChecked: Boolean
        get() =
            terms.filter(ConsentItem::isRequired).let { essentials ->
                essentials.isNotEmpty() && essentials.all(ConsentItem::isChecked)
            }

    /** 필수 항목이 모두 체크됐고 로딩 중이 아니면 제출할 수 있다. */
    val canSubmit: Boolean
        get() = isEssentialAllChecked && !isLoading
}

/** 약관 상세 시트에 표시할 문서 제목과 본문(마크다운). */
data class TermDetailContent(
    val title: String,
    val content: String,
)

sealed interface TermEffect : MviEffect {
    /** 약관 화면을 닫는다. */
    data object Closed : TermEffect

    /** 필수 권한이 없어 권한 동의 화면으로 이동한다. */
    data object DeniedPerm : TermEffect

    /** 프로필이 이미 등록돼 있어 홈으로 이동한다. */
    data object ExistProfile : TermEffect

    /** 프로필이 없어 온보딩으로 이동한다. */
    data object NonExistProfile : TermEffect

    /** 제출 실패 등 사용자에게 안내할 1회성 Toast를 표시한다. */
    data class ShowToast(
        val message: String,
    ) : TermEffect
}

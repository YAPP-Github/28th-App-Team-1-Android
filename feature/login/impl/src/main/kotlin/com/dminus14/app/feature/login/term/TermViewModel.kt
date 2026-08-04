package com.dminus14.app.feature.login.term

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.usecase.GetConsentDocumentUseCase
import com.dminus14.app.domain.usecase.GetPendingConsentListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermViewModel
@Inject
constructor(
    private val getPendingConsentList: GetPendingConsentListUseCase,
    private val getConsentDocument: GetConsentDocumentUseCase,
) : MviViewModel<TermIntent, TermState, TermEffect>(TermState()) {
    /** 테스트 전용: 초기 State를 주입한다. UseCase는 실제 fake로 넘겨야 한다. */
    internal constructor(
        getPendingConsentList: GetPendingConsentListUseCase,
        getConsentDocument: GetConsentDocumentUseCase,
        initialState: TermState,
    ) : this(getPendingConsentList, getConsentDocument) {
        reduce { initialState }
    }

    override fun onIntent(intent: TermIntent) {
        when (intent) {
            TermIntent.Load -> {
                loadPending()
            }

            TermIntent.ClickClose -> {
                sendEffect(TermEffect.Closed)
            }

            TermIntent.ClickAllAgree -> {
                toggleAllAgree()
            }

            is TermIntent.ClickTerm -> {
                toggleTerm(intent.index)
            }

            is TermIntent.ClickViewTerm -> {
                openTermDetail(intent.index)
            }

            TermIntent.DismissTermDetail -> {
                reduce { copy(visibleTermDetailIndex = null) }
            }

            TermIntent.ClickAgree -> {
                if (state.value.canSubmit) {
                    sendEffect(TermEffect.Agreed)
                }
            }
        }
    }

    private fun loadPending() {
        if (state.value.isLoading) return
        reduce { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getPendingConsentList()
                .onSuccess { pending ->
                    when (pending.status) {
                        ConsentPendingStatus.NOT_SUBMITTED,
                        ConsentPendingStatus.STALE,
                        ConsentPendingStatus.UNKNOWN,
                            -> {
                            reduce {
                                copy(
                                    terms = pending.items,
                                    isLoading = false,
                                )
                            }
                        }

                        ConsentPendingStatus.UP_TO_DATE -> {
                            // 프로필상태 확인
                        }
                    }
                }.onFailure { error ->
                    handleLoadFailure(error)
                }
        }
    }

    // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
    private suspend fun handleLoadFailure(error: Throwable) {
        reduce { copy(isLoading = false) }
        when (error) {
            is NetworkUnavailableException -> {
                GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
            }

            is ServerException -> {
                GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
            }

            else -> {
                GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
            }
        }
    }

    private fun toggleAllAgree() {
        val checked = !state.value.isAllChecked
        reduce {
            copy(terms = terms.map { it.copy(isChecked = checked) })
        }
    }

    private fun toggleTerm(index: Int) {
        reduce {
            copy(
                terms =
                    terms.mapIndexed { termIndex, term ->
                        if (termIndex == index) {
                            term.copy(isChecked = !term.isChecked)
                        } else {
                            term
                        }
                    },
            )
        }
    }

    /**
     * ClickViewTerm(index)로 본문을 조회한다. 로컬 body가 이미 있으면 즉시 시트를 열고,
     * 서버 문서가 있으면(hasDocument=true) rawCode·version으로 조회한 뒤 body를 채우고 시트를 연다.
     */
    @Suppress("detekt:ReturnCount") // 가드 절이 중첩보다 명확하다.
    private fun openTermDetail(index: Int) {
        val term = state.value.terms.getOrNull(index) ?: return

        if (!term.hasDocument) return
        if (state.value.isLoading) return

        reduce { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            getConsentDocument(rawCode = term.rawCode, version = term.version)
                .onSuccess { document ->
                    reduce {
                        copy(
                            isLoading = false,
                            visibleTermDetailIndex = index,
                            visibleTermDetail = TermDetailContent(
                                title = document.title,
                                content = document.contentMarkdown,
                            ),
                        )
                    }
                }.onFailure { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}

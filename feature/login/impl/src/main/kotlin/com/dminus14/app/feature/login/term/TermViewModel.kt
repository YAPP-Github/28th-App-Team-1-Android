package com.dminus14.app.feature.login.term

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermViewModel
    @Inject
    constructor() :
    MviViewModel<TermIntent, TermState, TermEffect>(TermState()) {
        internal constructor(initialState: TermState) : this() {
            reduce { initialState }
        }

        override fun onIntent(intent: TermIntent) {
            when (intent) {
                TermIntent.Load -> {}

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
                        // 약관 동의 및 권한 상태 확인
                    }
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

        private fun openTermDetail(index: Int) {
            val term = state.value.terms.getOrNull(index) ?: return
            if (term.body.isBlank()) return
            reduce { copy(visibleTermDetailIndex = index) }
        }
    }

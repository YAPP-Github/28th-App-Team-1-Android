package com.dminus14.app.feature.login.term

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermViewModel
    @Inject
    constructor() : MviViewModel<TermIntent, TermState, TermEffect>(TermState()) {
        override fun onIntent(intent: TermIntent) {
            when (intent) {
                TermIntent.Load -> {
                    reduce { copy(isLoading = false) }
                }

                TermIntent.ClickAgree -> {
                    sendEffect(TermEffect.Agreed)
                }
            }
        }
    }

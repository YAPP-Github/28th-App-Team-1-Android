package com.dminus14.app.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Feature ViewModel의 MVI 보일러플레이트를 담당하는 Base.
 *
 * StateFlow / Effect Channel / `reduce` / `sendEffect`만 제공하고,
 * UseCase 호출·에러 정책·Navigation 실행은 Feature ViewModel에 남긴다.
 *
 * @param initialState 화면 초기 State
 */
abstract class MviViewModel<I : MviIntent, S : MviState, E : MviEffect>(
    initialState: S,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /** 화면 이벤트 진입점. Feature에서 `when`으로 분기한다. */
    abstract fun onIntent(intent: I)

    protected fun reduce(reducer: S.() -> S) {
        _state.update(reducer)
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

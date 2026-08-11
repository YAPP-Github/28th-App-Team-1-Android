package com.dminus14.app.core.common.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

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

    /**
     * Effect를 채널에 즉시(non-blocking) 발행한다.
     *
     * 호출 즉시 [Channel.trySend]로 채널에 넣기 때문에 별도 코루틴을 생성하지 않고,
     * 호출 순서와 채널 진입 순서가 항상 일치한다. 단, 동시에 발행되는 Effect 수가
     * [Channel.BUFFERED] 용량을 넘거나 채널이 닫힌 경우 발행이 실패할 수 있으며,
     * 이 경우 Effect는 유실되고 경고 로그만 남긴다(화면 크래시로 이어지지 않음).
     */
    protected fun sendEffect(effect: E) {
        _effect
            .trySend(effect)
            .onFailure { cause ->
                // Effect data에는 인증 URL이나 불투명 미디어 참조가 포함될 수 있다.
                Log.w(TAG, "Effect 발행 실패: ${effect::class.simpleName}", cause)
            }
    }

    private companion object {
        const val TAG = "MviViewModel"
    }
}

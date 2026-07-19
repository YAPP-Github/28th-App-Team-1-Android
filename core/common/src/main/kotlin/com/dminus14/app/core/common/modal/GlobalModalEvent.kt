package com.dminus14.app.core.common.modal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DisposableHandle

class GlobalModalEvent(
    val request: GlobalModalRequest,
) {
    private val result = CompletableDeferred<GlobalModalResult>()

    val isActive: Boolean
        get() = result.isActive

    suspend fun awaitResult(): GlobalModalResult = result.await()

    fun complete(value: GlobalModalResult): Boolean = result.complete(value)

    fun cancel() {
        result.cancel(CancellationException("Global modal caller was cancelled"))
    }

    fun invokeOnCancellation(action: () -> Unit): DisposableHandle =
        result.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                action()
            }
        }
}

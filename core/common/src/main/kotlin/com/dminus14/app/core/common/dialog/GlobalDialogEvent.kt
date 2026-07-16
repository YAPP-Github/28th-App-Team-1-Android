package com.dminus14.app.core.common.dialog

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DisposableHandle

class GlobalDialogEvent(
    val request: GlobalDialogRequest,
) {
    private val result = CompletableDeferred<GlobalDialogResult>()

    val isActive: Boolean
        get() = result.isActive

    suspend fun awaitResult(): GlobalDialogResult = result.await()

    fun complete(value: GlobalDialogResult): Boolean = result.complete(value)

    fun cancel() {
        result.cancel(CancellationException("Global dialog caller was cancelled"))
    }

    fun invokeOnCancellation(action: () -> Unit): DisposableHandle =
        result.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                action()
            }
        }
}

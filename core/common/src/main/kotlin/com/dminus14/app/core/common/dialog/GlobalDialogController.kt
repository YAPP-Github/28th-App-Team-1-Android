package com.dminus14.app.core.common.dialog

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class GlobalDialogController {
    private val mutableEvents =
        MutableSharedFlow<GlobalDialogEvent>(
            replay = 0,
            extraBufferCapacity = 0,
        )

    val events: SharedFlow<GlobalDialogEvent> = mutableEvents.asSharedFlow()

    suspend fun show(request: GlobalDialogRequest): GlobalDialogResult {
        val event = GlobalDialogEvent(request)

        return try {
            mutableEvents.emit(event)
            event.awaitResult()
        } finally {
            event.cancel()
        }
    }
}

private val globalDialogController = GlobalDialogController()

val globalDialogEvents: SharedFlow<GlobalDialogEvent> = globalDialogController.events

suspend fun showGlobalDialog(request: GlobalDialogRequest): GlobalDialogResult =
    globalDialogController.show(request)

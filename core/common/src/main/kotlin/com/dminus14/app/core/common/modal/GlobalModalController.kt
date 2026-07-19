package com.dminus14.app.core.common.modal

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class GlobalModalController {
    private val mutableEvents =
        MutableSharedFlow<GlobalModalEvent>(
            replay = 0,
            extraBufferCapacity = 0,
        )

    val events: SharedFlow<GlobalModalEvent> = mutableEvents.asSharedFlow()

    suspend fun show(request: GlobalModalRequest): GlobalModalResult {
        val event = GlobalModalEvent(request)

        return try {
            mutableEvents.emit(event)
            event.awaitResult()
        } finally {
            event.cancel()
        }
    }
}

private val globalModalController = GlobalModalController()

val globalModalEvents: SharedFlow<GlobalModalEvent> = globalModalController.events

suspend fun showGlobalModal(request: GlobalModalRequest): GlobalModalResult =
    globalModalController.show(request)

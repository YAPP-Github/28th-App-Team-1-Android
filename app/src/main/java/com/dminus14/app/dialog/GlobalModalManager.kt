package com.dminus14.app.dialog

import com.dminus14.app.core.common.modal.GlobalModalEvent
import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.globalModalEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalModalManager internal constructor(
    private val events: Flow<GlobalModalEvent>,
    private val scope: CoroutineScope,
) {
    @Inject
    constructor() : this(
        events = globalModalEvents,
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    )

    private val pendingEvents = ArrayDeque<GlobalModalEvent>()
    private val spaceAvailable = Channel<Unit>(capacity = Channel.CONFLATED)
    private var collectionJob: Job? = null

    private val mutableCurrentModal = MutableStateFlow<GlobalModalEvent?>(null)
    val currentModal: StateFlow<GlobalModalEvent?> = mutableCurrentModal.asStateFlow()

    internal val pendingCount: Int
        get() = pendingEvents.size

    @Synchronized
    fun start() {
        if (collectionJob != null) return

        collectionJob =
            scope.launch {
                events.collect(::enqueue)
            }
    }

    fun confirm(event: GlobalModalEvent) {
        resolve(event, GlobalModalResult.Confirm)
    }

    fun cancel(event: GlobalModalEvent) {
        resolve(event, GlobalModalResult.Cancel)
    }

    fun dismiss(event: GlobalModalEvent) {
        resolve(event, GlobalModalResult.Dismiss)
    }

    private suspend fun enqueue(event: GlobalModalEvent) {
        event.invokeOnCancellation {
            scope.launch { removeCancelled(event) }
        }

        if (event.isActive) {
            if (mutableCurrentModal.value == null) {
                mutableCurrentModal.value = event
            } else {
                enqueuePending(event)
            }
        }
    }

    private suspend fun enqueuePending(event: GlobalModalEvent) {
        var handled = false

        while (event.isActive && !handled) {
            handled = tryEnqueue(event)
            if (!handled) {
                spaceAvailable.receive()
            }
        }
    }

    private fun tryEnqueue(event: GlobalModalEvent): Boolean {
        val dismissibleIndex =
            pendingEvents.indexOfFirst { pending -> pending.request.dismissible }

        return when {
            pendingEvents.size < MAX_PENDING_DIALOGS -> {
                pendingEvents.addLast(event)
                true
            }

            dismissibleIndex >= 0 -> {
                pendingEvents
                    .removeAt(dismissibleIndex)
                    .complete(GlobalModalResult.DroppedByOverflow)
                pendingEvents.addLast(event)
                true
            }

            event.request.dismissible -> {
                event.complete(GlobalModalResult.DroppedByOverflow)
                true
            }

            else -> {
                false
            }
        }
    }

    private fun resolve(
        event: GlobalModalEvent,
        result: GlobalModalResult,
    ) {
        scope.launch {
            if (mutableCurrentModal.value !== event) return@launch

            event.complete(result)
            mutableCurrentModal.value = null
            promoteNext()
        }
    }

    private fun removeCancelled(event: GlobalModalEvent) {
        if (mutableCurrentModal.value === event) {
            mutableCurrentModal.value = null
            promoteNext()
            return
        }

        if (pendingEvents.remove(event)) {
            spaceAvailable.trySend(Unit)
            return
        }

        spaceAvailable.trySend(Unit)
    }

    private fun promoteNext() {
        while (pendingEvents.isNotEmpty()) {
            val next = pendingEvents.removeFirst()
            spaceAvailable.trySend(Unit)

            if (next.isActive) {
                mutableCurrentModal.value = next
                return
            }
        }
    }

    private companion object {
        const val MAX_PENDING_DIALOGS = 10
    }
}

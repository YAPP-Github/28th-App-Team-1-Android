package com.dminus14.app.dialog

import com.dminus14.app.core.common.dialog.GlobalDialogEvent
import com.dminus14.app.core.common.dialog.GlobalDialogResult
import com.dminus14.app.core.common.dialog.globalDialogEvents
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
class GlobalDialogManager internal constructor(
    private val events: Flow<GlobalDialogEvent>,
    private val scope: CoroutineScope,
) {
    @Inject
    constructor() : this(
        events = globalDialogEvents,
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
    )

    private val pendingEvents = ArrayDeque<GlobalDialogEvent>()
    private val spaceAvailable = Channel<Unit>(capacity = Channel.CONFLATED)
    private var collectionJob: Job? = null

    private val mutableCurrentDialog = MutableStateFlow<GlobalDialogEvent?>(null)
    val currentDialog: StateFlow<GlobalDialogEvent?> = mutableCurrentDialog.asStateFlow()

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

    fun confirm(event: GlobalDialogEvent) {
        resolve(event, GlobalDialogResult.Confirm)
    }

    fun cancel(event: GlobalDialogEvent) {
        resolve(event, GlobalDialogResult.Cancel)
    }

    fun dismiss(event: GlobalDialogEvent) {
        resolve(event, GlobalDialogResult.Dismiss)
    }

    private suspend fun enqueue(event: GlobalDialogEvent) {
        event.invokeOnCancellation {
            scope.launch { removeCancelled(event) }
        }

        if (event.isActive) {
            if (mutableCurrentDialog.value == null) {
                mutableCurrentDialog.value = event
            } else {
                enqueuePending(event)
            }
        }
    }

    private suspend fun enqueuePending(event: GlobalDialogEvent) {
        var handled = false

        while (event.isActive && !handled) {
            handled = tryEnqueue(event)
            if (!handled) {
                spaceAvailable.receive()
            }
        }
    }

    private fun tryEnqueue(event: GlobalDialogEvent): Boolean {
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
                    .complete(GlobalDialogResult.DroppedByOverflow)
                pendingEvents.addLast(event)
                true
            }

            event.request.dismissible -> {
                event.complete(GlobalDialogResult.DroppedByOverflow)
                true
            }

            else -> {
                false
            }
        }
    }

    private fun resolve(
        event: GlobalDialogEvent,
        result: GlobalDialogResult,
    ) {
        scope.launch {
            if (mutableCurrentDialog.value !== event) return@launch

            event.complete(result)
            mutableCurrentDialog.value = null
            promoteNext()
        }
    }

    private fun removeCancelled(event: GlobalDialogEvent) {
        if (mutableCurrentDialog.value === event) {
            mutableCurrentDialog.value = null
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
                mutableCurrentDialog.value = next
                return
            }
        }
    }

    private companion object {
        const val MAX_PENDING_DIALOGS = 10
    }
}

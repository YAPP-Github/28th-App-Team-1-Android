package com.dminus14.app.dialog

import com.dminus14.app.core.common.dialog.GlobalDialogEvent
import com.dminus14.app.core.common.dialog.GlobalDialogRequest
import com.dminus14.app.core.common.dialog.GlobalDialogResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalDialogManagerTest {
    @Test
    fun `start is idempotent and consumes an event once`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = GlobalDialogManager(events, backgroundScope)
            manager.start()
            manager.start()
            runCurrent()
            val event = event("current")

            emit(events, event)

            assertSame(event, manager.currentDialog.value)
            assertEquals(0, manager.pendingCount)
        }

    @Test
    fun `completed dialogs promote pending events in FIFO order`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val first = event("first")
            val second = event("second")
            val third = event("third")
            emit(events, first)
            emit(events, second)
            emit(events, third)

            manager.confirm(first)
            runCurrent()
            assertSame(second, manager.currentDialog.value)

            manager.cancel(second)
            runCurrent()
            assertSame(third, manager.currentDialog.value)
            assertEquals(GlobalDialogResult.Confirm, first.awaitResult())
            assertEquals(GlobalDialogResult.Cancel, second.awaitResult())
        }

    @Test
    fun `overflow drops the oldest dismissible pending event`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val current = event("current", dismissible = false)
            emit(events, current)
            val pending =
                List(10) { index ->
                    event(
                        title = "pending-$index",
                        dismissible = index == 3,
                    ).also { emit(events, it) }
                }
            val incoming = event("incoming", dismissible = false)

            emit(events, incoming)

            assertEquals(GlobalDialogResult.DroppedByOverflow, pending[3].awaitResult())
            assertEquals(10, manager.pendingCount)
            assertTrue(incoming.isActive)
        }

    @Test
    fun `overflow drops a dismissible incoming event when protected queue is full`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            emit(events, event("current", dismissible = false))
            repeat(10) { index -> emit(events, event("pending-$index", dismissible = false)) }
            val incoming = event("incoming", dismissible = true)

            emit(events, incoming)

            assertEquals(GlobalDialogResult.DroppedByOverflow, incoming.awaitResult())
            assertEquals(10, manager.pendingCount)
        }

    @Test
    fun `protected incoming event waits until full protected queue has space`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val current = event("current", dismissible = false)
            emit(events, current)
            val pending =
                List(10) { index ->
                    event("pending-$index", dismissible = false).also { emit(events, it) }
                }
            val incoming = event("incoming", dismissible = false)

            emit(events, incoming)
            assertEquals(10, manager.pendingCount)
            assertTrue(incoming.isActive)

            manager.confirm(current)
            runCurrent()

            assertSame(pending.first(), manager.currentDialog.value)
            assertEquals(10, manager.pendingCount)
            assertTrue(incoming.isActive)
        }

    @Test
    fun `cancelling protected event while it waits for queue space prevents enqueue`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val current = event("current", dismissible = false)
            emit(events, current)
            val firstPending = event("pending-0", dismissible = false)
            emit(events, firstPending)
            repeat(9) { index -> emit(events, event("pending-${index + 1}", dismissible = false)) }
            val incoming = event("incoming", dismissible = false)
            emit(events, incoming)

            incoming.cancel()
            runCurrent()
            manager.confirm(current)
            runCurrent()

            assertFalse(incoming.isActive)
            assertSame(firstPending, manager.currentDialog.value)
            assertEquals(9, manager.pendingCount)
        }

    @Test
    fun `cancelling current dialog promotes next event`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val current = event("current")
            val next = event("next")
            emit(events, current)
            emit(events, next)

            current.cancel()
            runCurrent()

            assertFalse(current.isActive)
            assertSame(next, manager.currentDialog.value)
        }

    @Test
    fun `cancelling pending dialog removes it from the queue`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val current = event("current")
            val cancelled = event("cancelled")
            val next = event("next")
            emit(events, current)
            emit(events, cancelled)
            emit(events, next)

            cancelled.cancel()
            runCurrent()
            manager.dismiss(current)
            runCurrent()

            assertSame(next, manager.currentDialog.value)
            assertEquals(0, manager.pendingCount)
        }

    @Test
    fun `current dialog remains available while no host observes it`() =
        runTest {
            val events = MutableSharedFlow<GlobalDialogEvent>()
            val manager = startedManager(events)
            val backgroundEvent = event("background")

            emit(events, backgroundEvent)
            runCurrent()

            assertSame(backgroundEvent, manager.currentDialog.value)
            assertTrue(backgroundEvent.isActive)
        }

    private fun TestScope.startedManager(
        events: MutableSharedFlow<GlobalDialogEvent>,
    ): GlobalDialogManager =
        GlobalDialogManager(events, backgroundScope).also {
            it.start()
            runCurrent()
        }

    private fun TestScope.emit(
        events: MutableSharedFlow<GlobalDialogEvent>,
        event: GlobalDialogEvent,
    ) {
        launch { events.emit(event) }
        runCurrent()
    }

    private fun event(
        title: String,
        dismissible: Boolean = true,
    ) = GlobalDialogEvent(
        GlobalDialogRequest(
            title = title,
            message = "Synthetic message",
            confirmText = "Confirm",
            dismissible = dismissible,
        ),
    )
}

package com.dminus14.app.core.common.dialog

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalDialogControllerTest {
    @Test
    fun `show suspends until event is completed and returns its result`() =
        runTest {
            val controller = GlobalDialogController()
            val receivedEvent = async { controller.events.first() }
            runCurrent()

            val result = async { controller.show(request()) }
            runCurrent()

            assertTrue(result.isActive)
            receivedEvent.await().complete(GlobalDialogResult.Confirm)

            assertEquals(GlobalDialogResult.Confirm, result.await())
        }

    @Test
    fun `show returns every supported completion result`() =
        runTest {
            val supportedResults =
                listOf(
                    GlobalDialogResult.Confirm,
                    GlobalDialogResult.Cancel,
                    GlobalDialogResult.Dismiss,
                    GlobalDialogResult.DroppedByOverflow,
                )

            supportedResults.forEach { expected ->
                val controller = GlobalDialogController()
                val receivedEvent = async { controller.events.first() }
                runCurrent()
                val result = async { controller.show(request()) }
                runCurrent()

                receivedEvent.await().complete(expected)

                assertEquals(expected, result.await())
            }
        }

    @Test
    fun `cancelling show cancels the delivered event`() =
        runTest {
            val controller = GlobalDialogController()
            val receivedEvent = async { controller.events.first() }
            runCurrent()
            val caller = launch { controller.show(request()) }
            runCurrent()
            val event = receivedEvent.await()

            caller.cancelAndJoin()

            assertFalse(event.isActive)
        }

    private fun request() =
        GlobalDialogRequest(
            title = "Synthetic title",
            message = "Synthetic message",
            confirmText = "Confirm",
        )
}

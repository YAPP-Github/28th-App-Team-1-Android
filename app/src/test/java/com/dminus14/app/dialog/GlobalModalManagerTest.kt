package com.dminus14.app.dialog

import com.dminus14.app.core.common.modal.GlobalModalEvent
import com.dminus14.app.core.common.modal.GlobalModalRequest
import com.dminus14.app.core.common.modal.GlobalModalResult
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
class GlobalModalManagerTest {
    @Test
    fun `start를 여러 번 호출해도 이벤트를 한 번만 소비한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
            val manager = GlobalModalManager(events, backgroundScope)
            manager.start()
            manager.start()
            runCurrent()
            val event = event("current")

            emit(events, event)

            assertSame(event, manager.currentModal.value)
            assertEquals(0, manager.pendingCount)
        }

    @Test
    fun `다이얼로그를 완료하면 대기 이벤트를 선입선출 순서로 표시한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
            val manager = startedManager(events)
            val first = event("first")
            val second = event("second")
            val third = event("third")
            emit(events, first)
            emit(events, second)
            emit(events, third)

            manager.confirm(first)
            runCurrent()
            assertSame(second, manager.currentModal.value)

            manager.cancel(second)
            runCurrent()
            assertSame(third, manager.currentModal.value)
            assertEquals(GlobalModalResult.Confirm, first.awaitResult())
            assertEquals(GlobalModalResult.Cancel, second.awaitResult())
        }

    @Test
    fun `대기열이 넘치면 닫을 수 있는 가장 오래된 대기 이벤트를 제거한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
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

            assertEquals(GlobalModalResult.DroppedByOverflow, pending[3].awaitResult())
            assertEquals(10, manager.pendingCount)
            assertTrue(incoming.isActive)
        }

    @Test
    fun `보호된 대기열이 가득 차면 새로 들어온 닫을 수 있는 이벤트를 제거한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
            val manager = startedManager(events)
            emit(events, event("current", dismissible = false))
            repeat(10) { index -> emit(events, event("pending-$index", dismissible = false)) }
            val incoming = event("incoming", dismissible = true)

            emit(events, incoming)

            assertEquals(GlobalModalResult.DroppedByOverflow, incoming.awaitResult())
            assertEquals(10, manager.pendingCount)
        }

    @Test
    fun `새로 들어온 보호 이벤트는 가득 찬 보호 대기열에 공간이 생길 때까지 기다린다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
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

            assertSame(pending.first(), manager.currentModal.value)
            assertEquals(10, manager.pendingCount)
            assertTrue(incoming.isActive)
        }

    @Test
    fun `대기열 공간을 기다리는 보호 이벤트를 취소하면 대기열에 추가하지 않는다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
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
            assertSame(firstPending, manager.currentModal.value)
            assertEquals(9, manager.pendingCount)
        }

    @Test
    fun `현재 다이얼로그를 취소하면 다음 이벤트를 표시한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
            val manager = startedManager(events)
            val current = event("current")
            val next = event("next")
            emit(events, current)
            emit(events, next)

            current.cancel()
            runCurrent()

            assertFalse(current.isActive)
            assertSame(next, manager.currentModal.value)
        }

    @Test
    fun `대기 중인 다이얼로그를 취소하면 대기열에서 제거한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
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

            assertSame(next, manager.currentModal.value)
            assertEquals(0, manager.pendingCount)
        }

    @Test
    fun `호스트가 관찰하지 않아도 현재 다이얼로그를 유지한다`() =
        runTest {
            val events = MutableSharedFlow<GlobalModalEvent>()
            val manager = startedManager(events)
            val backgroundEvent = event("background")

            emit(events, backgroundEvent)
            runCurrent()

            assertSame(backgroundEvent, manager.currentModal.value)
            assertTrue(backgroundEvent.isActive)
        }

    private fun TestScope.startedManager(
        events: MutableSharedFlow<GlobalModalEvent>,
    ): GlobalModalManager =
        GlobalModalManager(events, backgroundScope).also {
            it.start()
            runCurrent()
        }

    private fun TestScope.emit(
        events: MutableSharedFlow<GlobalModalEvent>,
        event: GlobalModalEvent,
    ) {
        launch { events.emit(event) }
        runCurrent()
    }

    private fun event(
        title: String,
        dismissible: Boolean = true,
    ) = GlobalModalEvent(
        GlobalModalRequest(
            title = title,
            message = "Synthetic message",
            confirmText = "Confirm",
            dismissible = dismissible,
        ),
    )
}

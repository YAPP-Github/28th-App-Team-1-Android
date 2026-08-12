package com.dminus14.app.feature.interview.interview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewTimerCoordinatorTest {
    @Test
    fun `시간 점프로 여러 경계를 지나도 각 사건은 한 번만 발생한다`() {
        val coordinator = InterviewTimerCoordinator()

        val events = coordinator.update(720_000L)
        val repeated = coordinator.update(720_000L)

        assertEquals(
            listOf(
                InterviewTimerCoordinator.Event.EARLY_FINISH,
                InterviewTimerCoordinator.Event.WRAP_UP,
                InterviewTimerCoordinator.Event.COUNTDOWN,
                InterviewTimerCoordinator.Event.HARD_CAP,
            ),
            events,
        )
        assertTrue(repeated.isEmpty())
    }

    @Test
    fun `감소한 시간 입력은 새로운 경계 사건을 만들지 않는다`() {
        val coordinator = InterviewTimerCoordinator()
        coordinator.update(500_000L)

        assertTrue(coordinator.update(100_000L).isEmpty())
    }

    @Test
    fun `복구한 경과 시간 이전의 경계 사건은 다시 발생하지 않는다`() {
        val coordinator = InterviewTimerCoordinator()

        coordinator.restore(710_000L)

        assertTrue(coordinator.update(710_000L).isEmpty())
        assertEquals(
            listOf(InterviewTimerCoordinator.Event.HARD_CAP),
            coordinator.update(720_000L),
        )
    }
}

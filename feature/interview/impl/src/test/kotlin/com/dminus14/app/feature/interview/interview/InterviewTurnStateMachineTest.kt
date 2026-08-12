package com.dminus14.app.feature.interview.interview

import com.dminus14.app.domain.model.InterviewAnswerEndRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewTurnStateMachineTest {
    @Test
    fun `동시에 두 제출을 시작할 수 없다`() {
        val machine = InterviewTurnStateMachine()

        assertTrue(machine.beginSubmission())
        assertFalse(machine.beginSubmission())
    }

    @Test
    fun `첫 503은 자동 재시도하고 두 번째는 사용자 복구로 전환한다`() {
        val machine = InterviewTurnStateMachine()
        machine.beginSubmission()

        assertEquals(
            InterviewTurnStateMachine.TemporaryFailureAction.RETRY_AUTOMATICALLY,
            machine.recordTemporaryFailure(),
        )
        assertEquals(
            InterviewTurnStateMachine.TemporaryFailureAction.REQUIRE_USER_ACTION,
            machine.recordTemporaryFailure(),
        )
        assertFalse(machine.isSubmitting)
    }

    @Test
    fun `제출 중 들어온 종료 의도는 최초 요청 하나만 보존한다`() {
        val machine = InterviewTurnStateMachine()
        machine.queueEnd(InterviewAnswerEndRequest.HardCap)
        machine.queueEnd(InterviewAnswerEndRequest.BackExit)

        assertEquals(InterviewAnswerEndRequest.HardCap, machine.consumePendingEnd())
        assertNull(machine.consumePendingEnd())
    }
}

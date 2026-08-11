package com.dminus14.app.feature.interview.interview

import com.dminus14.app.feature.interview.InterviewConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterviewContractTest {
    @Test
    fun `모든 준비 조건을 만족해야 시작할 수 있다`() {
        val state =
            InterviewState(
                isCameraPermissionGranted = true,
                isCameraReady = true,
                isMicrophoneReady = true,
                isServerReady = true,
                hasEnoughStorage = true,
            )

        assertTrue(state.isReadyToStart)
        assertFalse(state.copy(hasEnoughStorage = false).isReadyToStart)
    }

    @Test
    fun `8분과 8분 45초 경계에서 종료와 랩업 조건이 활성화된다`() {
        val early =
            InterviewState(
                elapsedMillis =
                    InterviewConstants.CAN_FINISH_INTERVIEW_SECONDS * 1_000L,
            )
        val wrapUp = InterviewState(elapsedMillis = InterviewConstants.WRAP_UP_SECONDS * 1_000L)

        assertTrue(early.canFinishEarly)
        assertFalse(early.isWrapUp)
        assertTrue(wrapUp.isWrapUp)
    }

    @Test
    fun `11분 50초부터 카운트다운을 계산하고 그 전에는 노출하지 않는다`() {
        val before = InterviewState(elapsedMillis = 709_000L)
        val started = InterviewState(elapsedMillis = 710_000L)
        val ended = InterviewState(elapsedMillis = 720_000L)

        assertNull(before.countdownSeconds)
        assertEquals(10, started.countdownSeconds)
        assertEquals(0, ended.countdownSeconds)
    }
}

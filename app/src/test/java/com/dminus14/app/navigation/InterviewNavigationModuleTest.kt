package com.dminus14.app.navigation

import com.dminus14.app.feature.home.api.Home
import com.dminus14.app.feature.interview.api.InterviewErrorRoute
import com.dminus14.app.feature.interview.api.InterviewErrorType
import com.dminus14.app.feature.interview.api.InterviewRoute
import com.dminus14.app.feature.interview.interview.InterviewCompletionReason
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import org.junit.Assert.assertEquals
import org.junit.Test

class InterviewNavigationModuleTest {
    @Test
    fun `면접 오류는 기존 면접 위에 추가하고 재개하면 오류만 제거한다`() {
        val navigator = Navigator(InterviewRoute)

        navigator.goTo(InterviewErrorRoute(InterviewErrorType.NETWORK))
        navigator.goBack()

        assertEquals(listOf(InterviewRoute), navigator.backStack)
    }

    @Test
    fun `면접 중단은 전체 스택을 홈으로 교체한다`() {
        val navigator = Navigator(InterviewRoute)
        navigator.goTo(InterviewErrorRoute(InterviewErrorType.SERVER_TEMPORARY))

        navigator.replaceAll(Home)

        assertEquals(listOf(Home), navigator.backStack)
    }

    @Test
    fun `면접이 정상 종료되면 홈으로 교체한 뒤 리포트를 push한다`() {
        val navigator = Navigator(InterviewRoute)
        val onInterviewEnded = { reason: InterviewCompletionReason, sessionId: Long ->
            navigator.replaceAll(Home)
            if (reason == InterviewCompletionReason.COMPLETED) {
                navigator.goTo(InterviewReport(sessionId = sessionId))
            }
        }

        onInterviewEnded(InterviewCompletionReason.COMPLETED, 1L)

        assertEquals(listOf(Home, InterviewReport(sessionId = 1L)), navigator.backStack)
    }

    @Test
    fun `면접이 중도 이탈로 종료되면 홈으로만 교체한다`() {
        val navigator = Navigator(InterviewRoute)
        val onInterviewEnded = { reason: InterviewCompletionReason, sessionId: Long ->
            navigator.replaceAll(Home)
            if (reason == InterviewCompletionReason.COMPLETED) {
                navigator.goTo(InterviewReport(sessionId = sessionId))
            }
        }

        onInterviewEnded(InterviewCompletionReason.ABANDONED, 1L)

        assertEquals(listOf(Home), navigator.backStack)
    }
}

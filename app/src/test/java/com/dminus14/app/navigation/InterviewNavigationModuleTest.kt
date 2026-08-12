package com.dminus14.app.navigation

import com.dminus14.app.feature.home.api.Home
import com.dminus14.app.feature.interview.api.InterviewErrorRoute
import com.dminus14.app.feature.interview.api.InterviewErrorType
import com.dminus14.app.feature.interview.api.InterviewRoute
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
    fun `면접 종료 결과를 받으면 전체 스택을 홈으로 교체한다`() {
        val navigator = Navigator(InterviewRoute)
        val onInterviewEnded = { navigator.replaceAll(Home) }

        onInterviewEnded()

        assertEquals(listOf(Home), navigator.backStack)
    }
}

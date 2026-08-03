package com.dminus14.app.navigation

import com.dminus14.app.feature.feedback.api.Feedback
import com.dminus14.app.feature.feedback.api.FeedbackOnboarding
import com.dminus14.app.feature.feedback.api.FeedbackReview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigatorTest {
    @Test
    fun `온보딩에서 평가와 검토로 이동하고 검토 뒤로가기는 평가로 돌아간다`() {
        val navigator = Navigator(FeedbackOnboarding("synthetic-token"))

        navigator.goTo(Feedback)
        navigator.goTo(FeedbackReview)
        navigator.goBack()

        assertEquals(
            listOf(FeedbackOnboarding("synthetic-token"), Feedback),
            navigator.backStack,
        )
    }

    @Test
    fun `루트에서 뒤로가면 등록된 앱 종료 동작을 호출한다`() {
        val navigator = Navigator(Feedback)
        var didExit = false
        navigator.onExit = { didExit = true }

        navigator.goBack()

        assertTrue(didExit)
        assertFalse(navigator.backStack.isEmpty())
    }
}

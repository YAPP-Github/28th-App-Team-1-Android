package com.dminus14.app.feature.feedback.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.feedback.api.Feedback
import com.dminus14.app.feature.feedback.api.FeedbackOnboarding
import com.dminus14.app.feature.feedback.api.FeedbackReview
import com.dminus14.app.feature.feedback.feedback.FeedbackScreen
import com.dminus14.app.feature.feedback.onboarding.FeedbackOnboardingScreen
import com.dminus14.app.feature.feedback.review.FeedbackReviewScreen

fun EntryProviderScope<Any>.feedbackEntryBuilder(
    goTo: (Any) -> Unit,
    goBack: () -> Unit,
) {
    entry<FeedbackOnboarding> { route ->
        FeedbackOnboardingScreen(
            token = route.token,
            onFeedbackReady = { goTo(Feedback) },
            onExit = goBack,
        )
    }
    entry<Feedback> {
        FeedbackScreen(
            onReviewReady = { goTo(FeedbackReview) },
            onExit = goBack,
        )
    }
    entry<FeedbackReview> {
        FeedbackReviewScreen(
            onReplayRequested = goBack,
            onCompleted = goBack,
            onExit = goBack,
        )
    }
}

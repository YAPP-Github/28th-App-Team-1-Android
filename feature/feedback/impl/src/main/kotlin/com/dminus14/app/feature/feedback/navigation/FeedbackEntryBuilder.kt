package com.dminus14.app.feature.feedback.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.feedback.api.Feedback
import com.dminus14.app.feature.feedback.api.FeedbackOnboarding
import com.dminus14.app.feature.feedback.api.FeedbackReview
import com.dminus14.app.feature.feedback.feedback.FeedbackScreen
import com.dminus14.app.feature.feedback.onboarding.FeedbackOnboardingScreen
import com.dminus14.app.feature.feedback.review.FeedbackReviewScreen

fun EntryProviderScope<Any>.feedbackEntryBuilder(
    onFeedbackReady: () -> Unit,
    onReviewReady: () -> Unit,
    onReplayRequested: () -> Unit,
    onExit: () -> Unit,
) {
    entry<FeedbackOnboarding> { route ->
        FeedbackOnboardingScreen(
            token = route.token,
            onFeedbackReady = onFeedbackReady,
            onExit = onExit,
        )
    }
    entry<Feedback> {
        FeedbackScreen(
            onReviewReady = onReviewReady,
            onExit = onExit,
        )
    }
    entry<FeedbackReview> {
        FeedbackReviewScreen(
            onReplayRequested = onReplayRequested,
            onCompleted = onExit,
            onExit = onExit,
        )
    }
}

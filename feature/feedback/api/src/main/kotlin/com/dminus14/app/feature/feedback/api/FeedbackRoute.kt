package com.dminus14.app.feature.feedback.api

class FeedbackOnboarding(
    val token: String,
) {
    override fun equals(other: Any?): Boolean = other is FeedbackOnboarding && token == other.token

    override fun hashCode(): Int = token.hashCode()

    override fun toString(): String = "FeedbackOnboarding(token=**redacted**)"
}

object Feedback

object FeedbackReview

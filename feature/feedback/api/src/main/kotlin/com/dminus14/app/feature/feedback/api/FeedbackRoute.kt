package com.dminus14.app.feature.feedback.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackOnboarding(
    val token: String,
) : NavKey {
    override fun equals(other: Any?): Boolean = other is FeedbackOnboarding && token == other.token

    override fun hashCode(): Int = token.hashCode()

    override fun toString(): String = "FeedbackOnboarding(token=**redacted**)"
}

@Serializable
data object Feedback : NavKey

@Serializable
data object FeedbackReview : NavKey

package com.dminus14.app.feature.feedback.review

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.domain.model.GuestFeedbackAxisCode

sealed interface FeedbackReviewIntent : MviIntent {
    data object LoadSession : FeedbackReviewIntent

    data object ReplayVideoClicked : FeedbackReviewIntent

    data class EditCommentClicked(
        val axis: GuestFeedbackAxisCode,
    ) : FeedbackReviewIntent

    data class CommentChanged(
        val value: String,
    ) : FeedbackReviewIntent

    data object CommentConfirmed : FeedbackReviewIntent

    data object CommentDismissed : FeedbackReviewIntent

    data object SubmitClicked : FeedbackReviewIntent

    data object SubmitConfirmed : FeedbackReviewIntent
}

data class FeedbackReviewState(
    val requesterName: String = "",
    val nickname: String = "",
    val axes: List<FeedbackReviewAxisUiModel> = emptyList(),
    val editingAxis: GuestFeedbackAxisCode? = null,
    val editingValue: String = "",
    val isCommentEditorVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val hasLoaded: Boolean = false,
) : MviState

data class FeedbackReviewAxisUiModel(
    val code: GuestFeedbackAxisCode,
    val title: String,
    val level: Int,
    val levelLabel: String,
    val comment: String,
) {
    val isPositive: Boolean
        get() = level >= 3
}

sealed interface FeedbackReviewEffect : MviEffect {
    data object ReplayRequested : FeedbackReviewEffect

    data object SubmissionCompleted : FeedbackReviewEffect

    data object ExitRequested : FeedbackReviewEffect

    data class ShowToast(
        val message: String,
    ) : FeedbackReviewEffect
}

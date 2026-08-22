package com.dminus14.app.feature.feedback.feedback

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.feature.feedback.session.CommentEditorState

sealed interface FeedbackIntent : MviIntent {
    data object LoadSession : FeedbackIntent

    data class AxisSelected(
        val axis: GuestFeedbackAxisCode,
    ) : FeedbackIntent

    data class RatingSelected(
        val axis: GuestFeedbackAxisCode,
        val level: Int,
    ) : FeedbackIntent

    data class CommentEditorClicked(
        val axis: GuestFeedbackAxisCode,
    ) : FeedbackIntent

    data class CommentChanged(
        val value: String,
    ) : FeedbackIntent

    data object CommentConfirmed : FeedbackIntent

    data object CommentDismissed : FeedbackIntent

    data object VideoExpanded : FeedbackIntent

    data object VideoIntroCompleted : FeedbackIntent

    data object VideoPlaybackFailed : FeedbackIntent

    data object ReviewClicked : FeedbackIntent

    data object BackPressed : FeedbackIntent
}

data class FeedbackState(
    val requesterName: String = "",
    val videoUrl: String = "",
    val axes: List<FeedbackAxisUiModel> = emptyList(),
    val selectedAxis: GuestFeedbackAxisCode? = null,
    val isCommentEditorVisible: Boolean = false,
    val editingAxis: GuestFeedbackAxisCode? = null,
    val editingValue: String = "",
    val isVideoExpanded: Boolean = true,
    val isVideoIntroVisible: Boolean = true,
    val hasLoaded: Boolean = false,
    val isPlaybackBlocked: Boolean = false,
) : MviState {
    val canReview: Boolean
        get() = axes.isNotEmpty() && axes.all { it.level != null }
}

internal fun FeedbackState.withEditorState(editorState: CommentEditorState): FeedbackState =
    copy(
        editingAxis = editorState.editingAxis,
        editingValue = editorState.editingValue,
        isCommentEditorVisible = editorState.isCommentEditorVisible,
    )

data class FeedbackAxisUiModel(
    val code: GuestFeedbackAxisCode,
    val title: String,
    val level: Int? = null,
    val comment: String = "",
)

data class FeedbackRatingOption(
    val level: Int,
    val label: String,
) {
    val isPositive: Boolean
        get() = level >= 3
}

sealed interface FeedbackEffect : MviEffect {
    data object ReviewReady : FeedbackEffect

    data object ExitRequested : FeedbackEffect
}

internal fun GuestFeedbackAxisCode.ratingOptions(): List<FeedbackRatingOption> {
    val labels =
        when (this) {
            GuestFeedbackAxisCode.GAZE -> listOf("잘 맞춤", "꽤 맞춤", "가끔 피함", "자주 피함")
            GuestFeedbackAxisCode.EXPRESSION -> listOf("안정됨", "꽤 안정됨", "가끔 굳음", "자주 굳음")
            GuestFeedbackAxisCode.POSTURE -> listOf("반듯함", "꽤 반듯함", "가끔 산만", "매우 산만")
            GuestFeedbackAxisCode.GESTURE -> listOf("잘 어울림", "꽤 어울림", "가끔 산만", "매우 산만")
            GuestFeedbackAxisCode.VOICE -> listOf("잘 들림", "꽤 들림", "꽤 안 들림", "안 들림")
        }
    return labels.mapIndexed {
        index,
        label,
        ->
        FeedbackRatingOption(level = 4 - index, label = label)
    }
}

internal fun GuestFeedbackAxisCode.question(requesterName: String): String =
    when (this) {
        GuestFeedbackAxisCode.GAZE -> "${requesterName}님은 눈을 잘 마주치나요?"
        GuestFeedbackAxisCode.EXPRESSION -> "표정이 안정되어 보이나요?"
        GuestFeedbackAxisCode.POSTURE -> "${requesterName}님이 자세를 잘 유지하나요?"
        GuestFeedbackAxisCode.GESTURE -> "손동작이 말과 잘 어울리나요?"
        GuestFeedbackAxisCode.VOICE -> "목소리가 선명하게 들리나요?"
    }

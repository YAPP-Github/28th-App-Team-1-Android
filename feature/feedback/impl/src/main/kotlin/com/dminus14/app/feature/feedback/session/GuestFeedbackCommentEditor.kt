package com.dminus14.app.feature.feedback.session

import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.usecase.SubmitGuestFeedbackUseCase

internal const val MAX_COMMENT_LENGTH = SubmitGuestFeedbackUseCase.MAX_COMMENT_LENGTH

data class CommentEditorState(
    val editingAxis: GuestFeedbackAxisCode? = null,
    val editingValue: String = "",
    val isCommentEditorVisible: Boolean = false,
) {
    companion object {
        val Hidden = CommentEditorState()
    }
}

class GuestFeedbackCommentEditor(
    private val session: GuestFeedbackFlowSession,
) {
    fun <T> open(
        axes: List<T>,
        axis: GuestFeedbackAxisCode,
        getCode: (T) -> GuestFeedbackAxisCode,
        getComment: (T) -> String,
    ): CommentEditorState? {
        val item = axes.firstOrNull { getCode(it) == axis } ?: return null
        return CommentEditorState(
            editingAxis = axis,
            editingValue = getComment(item),
            isCommentEditorVisible = true,
        )
    }

    fun <T> confirm(
        axes: List<T>,
        editingAxis: GuestFeedbackAxisCode?,
        editingValue: String,
        getCode: (T) -> GuestFeedbackAxisCode,
        updateComment: (T, String) -> T,
    ): Pair<List<T>, CommentEditorState>? {
        val axis = editingAxis ?: return null
        session.updateComment(axis, editingValue)
        val newAxes =
            axes.map { item ->
                if (getCode(item) == axis) {
                    updateComment(item, editingValue)
                } else {
                    item
                }
            }
        return newAxes to CommentEditorState.Hidden
    }

    fun dismiss(): CommentEditorState = CommentEditorState.Hidden
}

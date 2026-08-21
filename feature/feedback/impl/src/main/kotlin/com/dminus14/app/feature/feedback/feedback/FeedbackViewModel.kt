package com.dminus14.app.feature.feedback.feedback

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.modal.GlobalModalRequest
import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.showGlobalModal
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.feature.feedback.session.GuestFeedbackCommentEditor
import com.dminus14.app.feature.feedback.session.GuestFeedbackFlowSession
import com.dminus14.app.feature.feedback.session.MAX_COMMENT_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel
    @Inject
    constructor(
        private val session: GuestFeedbackFlowSession,
    ) : MviViewModel<FeedbackIntent, FeedbackState, FeedbackEffect>(FeedbackState()) {
        private val commentEditor = GuestFeedbackCommentEditor(session)

        override fun onIntent(intent: FeedbackIntent) {
            when (intent) {
                FeedbackIntent.LoadSession -> {
                    loadSession()
                }

                is FeedbackIntent.AxisSelected -> {
                    reduce {
                        copy(selectedAxis = intent.axis, isVideoExpanded = false)
                    }
                }

                is FeedbackIntent.RatingSelected -> {
                    selectRating(intent.axis, intent.level)
                }

                is FeedbackIntent.CommentEditorClicked -> {
                    openCommentEditor(intent.axis)
                }

                is FeedbackIntent.CommentChanged -> {
                    reduce {
                        copy(
                            editingValue = intent.value.take(MAX_COMMENT_LENGTH),
                        )
                    }
                }

                FeedbackIntent.CommentConfirmed -> {
                    confirmComment()
                }

                FeedbackIntent.CommentDismissed -> {
                    dismissComment()
                }

                FeedbackIntent.VideoExpanded -> {
                    reduce { copy(isVideoExpanded = true) }
                }

                FeedbackIntent.VideoIntroCompleted -> {
                    reduce { copy(isVideoIntroVisible = false) }
                }

                FeedbackIntent.VideoPlaybackFailed -> {
                    handlePlaybackFailure()
                }

                FeedbackIntent.ReviewClicked -> {
                    review()
                }

                FeedbackIntent.BackPressed -> {
                    confirmExit()
                }
            }
        }

        private fun loadSession() {
            if (state.value.hasLoaded) return
            val data =
                session.snapshot() ?: run {
                    sendEffect(FeedbackEffect.ExitRequested)
                    return
                }

            if (data.videoUrl.isBlank()) {
                handlePlaybackFailure()
            } else {
                reduce {
                    copy(
                        requesterName = data.requesterName,
                        videoUrl = data.videoUrl,
                        axes =
                            data.axes.map { axis ->
                                val draft = data.ratings[axis.code]
                                FeedbackAxisUiModel(
                                    code = axis.code,
                                    title = axis.code.title,
                                    level = draft?.level,
                                    comment = draft?.comment.orEmpty(),
                                )
                            },
                        hasLoaded = true,
                    )
                }
            }
        }

        private fun selectRating(
            axis: GuestFeedbackAxisCode,
            level: Int,
        ) {
            if (level !in MIN_RATING_LEVEL..MAX_RATING_LEVEL || state.value.isPlaybackBlocked) {
                return
            }
            session.updateLevel(axis, level)
            reduce {
                copy(
                    axes =
                        axes.map { item ->
                            if (item.code ==
                                axis
                            ) {
                                item.copy(level = level)
                            } else {
                                item
                            }
                        },
                )
            }
        }

        private fun openCommentEditor(axis: GuestFeedbackAxisCode) {
            val editorState =
                commentEditor.open(
                    axes = state.value.axes,
                    axis = axis,
                    getCode = { it.code },
                    getComment = { it.comment },
                ) ?: return
            reduce { withEditorState(editorState) }
        }

        private fun confirmComment() {
            val (newAxes, editorState) =
                commentEditor.confirm(
                    axes = state.value.axes,
                    editingAxis = state.value.editingAxis,
                    editingValue = state.value.editingValue,
                    getCode = { it.code },
                    updateComment = { item, comment -> item.copy(comment = comment) },
                ) ?: return
            reduce { copy(axes = newAxes).withEditorState(editorState) }
        }

        private fun dismissComment() {
            val editorState = commentEditor.dismiss()
            reduce { withEditorState(editorState) }
        }

        private fun review() {
            if (state.value.canReview && !state.value.isPlaybackBlocked) {
                sendEffect(FeedbackEffect.ReviewReady)
            }
        }

        private fun confirmExit() {
            if (state.value.isPlaybackBlocked) return

            viewModelScope.launch {
                val result =
                    showGlobalModal(
                        GlobalModalRequest(
                            title = "피드백을 종료하시겠습니까?",
                            message = "",
                            confirmText = "종료하기",
                            cancelText = "계속 작성",
                            dismissible = true,
                        ),
                    )
                if (result == GlobalModalResult.Confirm) {
                    session.clear()
                    sendEffect(FeedbackEffect.ExitRequested)
                }
            }
        }

        private fun handlePlaybackFailure() {
            if (state.value.isPlaybackBlocked) return
            val data = session.snapshot()
            val axes =
                data
                    ?.axes
                    ?.map { axis ->
                        val draft = data.ratings[axis.code]
                        FeedbackAxisUiModel(
                            code = axis.code,
                            title = axis.code.title,
                            level = draft?.level,
                            comment = draft?.comment.orEmpty(),
                        )
                    }.orEmpty()
            reduce { copy(isPlaybackBlocked = true, axes = axes) }
            viewModelScope.launch {
                val result =
                    showGlobalModal(
                        GlobalModalRequest(
                            title = "영상을 재생할 수 없어요",
                            message = "영상 재생 중 문제가 발생했어요. 앱을 종료한 뒤 다시 시도해주세요.",
                            confirmText = "종료하기",
                            dismissible = false,
                        ),
                    )
                if (result == GlobalModalResult.Confirm) {
                    session.clear()
                    sendEffect(FeedbackEffect.ExitRequested)
                }
            }
        }
    }

internal val GuestFeedbackAxisCode.title: String
    get() =
        when (this) {
            GuestFeedbackAxisCode.GAZE -> "시선"
            GuestFeedbackAxisCode.EXPRESSION -> "표정"
            GuestFeedbackAxisCode.POSTURE -> "자세"
            GuestFeedbackAxisCode.GESTURE -> "손동작"
            GuestFeedbackAxisCode.VOICE -> "목소리"
        }

private const val MIN_RATING_LEVEL = 1
private const val MAX_RATING_LEVEL = 4

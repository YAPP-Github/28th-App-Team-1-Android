package com.dminus14.app.feature.feedback.review

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.modal.GlobalModalRequest
import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.showGlobalModal
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.GuestFeedbackAlreadySubmittedException
import com.dminus14.app.domain.exception.GuestFeedbackCapacityFullException
import com.dminus14.app.domain.exception.GuestFeedbackRequestException
import com.dminus14.app.domain.exception.GuestFeedbackShareClosedException
import com.dminus14.app.domain.exception.GuestFeedbackValidationException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackRating
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.usecase.SubmitGuestFeedbackUseCase
import com.dminus14.app.feature.feedback.feedback.ratingOptions
import com.dminus14.app.feature.feedback.feedback.title
import com.dminus14.app.feature.feedback.session.GuestFeedbackFlowSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackReviewViewModel
    @Inject
    constructor(
        private val submitGuestFeedbackUseCase: SubmitGuestFeedbackUseCase,
        private val session: GuestFeedbackFlowSession,
    ) : MviViewModel<FeedbackReviewIntent, FeedbackReviewState, FeedbackReviewEffect>(
            FeedbackReviewState(),
        ) {
        private var isConfirmationPending = false

        override fun onIntent(intent: FeedbackReviewIntent) {
            if (state.value.isSubmitting && intent != FeedbackReviewIntent.LoadSession) return

            when (intent) {
                FeedbackReviewIntent.LoadSession -> {
                    loadSession()
                }

                FeedbackReviewIntent.ReplayVideoClicked -> {
                    sendEffect(
                        FeedbackReviewEffect.ReplayRequested,
                    )
                }

                is FeedbackReviewIntent.EditCommentClicked -> {
                    openCommentEditor(intent.axis)
                }

                is FeedbackReviewIntent.CommentChanged -> {
                    reduce {
                        copy(
                            editingValue = intent.value.take(MAX_COMMENT_LENGTH),
                        )
                    }
                }

                FeedbackReviewIntent.CommentConfirmed -> {
                    confirmComment()
                }

                FeedbackReviewIntent.CommentDismissed -> {
                    dismissComment()
                }

                FeedbackReviewIntent.SubmitClicked -> {
                    showSubmitConfirmation()
                }

                FeedbackReviewIntent.SubmitConfirmed -> {
                    submit()
                }
            }
        }

        private fun loadSession() {
            if (state.value.hasLoaded) return
            val data =
                session.snapshot() ?: run {
                    sendEffect(FeedbackReviewEffect.ExitRequested)
                    return
                }
            val axes =
                data.axes.mapNotNull { axis ->
                    val draft = data.ratings[axis.code]
                    val level = draft?.level ?: return@mapNotNull null
                    FeedbackReviewAxisUiModel(
                        code = axis.code,
                        title = axis.code.title,
                        level = level,
                        levelLabel =
                            axis.code
                                .ratingOptions()
                                .firstOrNull { it.level == level }
                                ?.label ?: return@mapNotNull null,
                        comment = draft.comment,
                    )
                }
            reduce {
                copy(
                    requesterName = data.requesterName,
                    nickname = data.nickname,
                    axes = axes,
                    hasLoaded = true,
                )
            }
        }

        private fun openCommentEditor(axis: GuestFeedbackAxisCode) {
            val item = state.value.axes.firstOrNull { it.code == axis } ?: return
            reduce {
                copy(
                    editingAxis = axis,
                    editingValue = item.comment,
                    isCommentEditorVisible = true,
                )
            }
        }

        private fun confirmComment() {
            val axis = state.value.editingAxis ?: return
            val comment = state.value.editingValue
            session.updateComment(axis, comment)
            reduce {
                copy(
                    axes =
                        axes.map { item ->
                            if (item.code ==
                                axis
                            ) {
                                item.copy(comment = comment)
                            } else {
                                item
                            }
                        },
                    editingAxis = null,
                    editingValue = "",
                    isCommentEditorVisible = false,
                )
            }
        }

        private fun dismissComment() {
            reduce { copy(editingAxis = null, editingValue = "", isCommentEditorVisible = false) }
        }

        private fun showSubmitConfirmation() {
            if (state.value.isSubmitting || isConfirmationPending) return
            isConfirmationPending = true
            viewModelScope.launch {
                val result =
                    showGlobalModal(
                        GlobalModalRequest(
                            title = "피드백 제출",
                            message = "제출하면 다시 고칠 수 없어요.",
                            confirmText = "제출",
                            cancelText = "취소",
                            dismissible = true,
                        ),
                    )
                isConfirmationPending = false
                if (result == GlobalModalResult.Confirm) {
                    onIntent(FeedbackReviewIntent.SubmitConfirmed)
                }
            }
        }

        private fun submit() {
            if (state.value.isSubmitting) return
            val data =
                session.snapshot() ?: run {
                    sendEffect(FeedbackReviewEffect.ExitRequested)
                    return
                }
            val ratings =
                data.axes.mapNotNull { axis ->
                    val draft = data.ratings[axis.code] ?: return@mapNotNull null
                    val level = draft.level ?: return@mapNotNull null
                    GuestFeedbackRating(axis.code, level, draft.comment)
                }

            reduce {
                copy(
                    isSubmitting = true,
                    editingAxis = null,
                    editingValue = "",
                    isCommentEditorVisible = false,
                )
            }
            viewModelScope.launch {
                submitGuestFeedbackUseCase(
                    token = data.token,
                    axes = data.axes,
                    submission = GuestFeedbackSubmission(data.nickname, ratings),
                ).onSuccess {
                    session.clear()
                    reduce { copy(isSubmitting = false) }
                    sendEffect(FeedbackReviewEffect.ShowToast("피드백을 제출했어요."))
                    sendEffect(FeedbackReviewEffect.SubmissionCompleted)
                }.onFailure(::handleFailure)
            }
        }

        private fun handleFailure(error: Throwable) {
            reduce { copy(isSubmitting = false) }
            viewModelScope.launch {
                when (error) {
                    is GuestFeedbackValidationException -> {
                        showGlobalModal(
                            GlobalModalRequest(
                                title = "피드백을 제출할 수 없어요",
                                message = "작성한 내용을 확인한 뒤 다시 시도해주세요.",
                                confirmText = "확인",
                                dismissible = true,
                            ),
                        )
                    }

                    is GuestFeedbackRequestException -> {
                        showExitError(
                            "요청 오류",
                            "서버 요청에 실패했습니다. 앱을 재실행하고 다시 시도해주세요.",
                        )
                    }

                    is GuestFeedbackShareClosedException -> {
                        showExitError(
                            "피드백 기간 종료",
                            "피드백 가능한 기간이 지났습니다.",
                        )
                    }

                    is GuestFeedbackCapacityFullException -> {
                        showExitError(
                            "피드백 마감",
                            "최대 피드백 가능 인원을 초과하여 더 이상 피드백을 받을 수 없습니다.",
                        )
                    }

                    is GuestFeedbackAlreadySubmittedException -> {
                        showExitError(
                            "이미 제출한 피드백",
                            "이미 이 기기에서 피드백한 이력이 있습니다. 중복 피드백은 할 수 없습니다.",
                        )
                    }

                    is NetworkUnavailableException -> {
                        session.clear()
                        GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                    }

                    is ServerException -> {
                        session.clear()
                        GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                    }

                    else -> {
                        GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
                    }
                }
            }
        }

        private suspend fun showExitError(
            title: String,
            message: String,
        ) {
            val result =
                showGlobalModal(
                    GlobalModalRequest(
                        title = title,
                        message = message,
                        confirmText = "종료하기",
                        dismissible = false,
                    ),
                )
            if (result == GlobalModalResult.Confirm) {
                session.clear()
                sendEffect(FeedbackReviewEffect.ExitRequested)
            }
        }
    }

private const val MAX_COMMENT_LENGTH = 100

package com.dminus14.app.feature.interview.error

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.AiTemporarilyUnavailableException
import com.dminus14.app.domain.exception.InterviewSessionAlreadyEndedException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.InterviewAbandonRequestCause
import com.dminus14.app.domain.model.InterviewEndType
import com.dminus14.app.domain.model.InterviewResumeState
import com.dminus14.app.domain.model.InterviewTerminalStatus
import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.usecase.AbandonInterviewUseCase
import com.dminus14.app.domain.usecase.ConfirmInterviewResumeUseCase
import com.dminus14.app.domain.usecase.GetInterviewMediaManifestUseCase
import com.dminus14.app.domain.usecase.GetInterviewProgressUseCase
import com.dminus14.app.domain.usecase.GetInterviewResumeUseCase
import com.dminus14.app.domain.usecase.RetainInterviewSessionForCleanupUseCase
import com.dminus14.app.domain.usecase.SavePendingInterviewAnswerUseCase
import com.dminus14.app.domain.usecase.SubmitAnswerUseCase
import com.dminus14.app.feature.interview.api.InterviewErrorType
import com.dminus14.app.feature.interview.interview.InterviewCompletionReason
import com.dminus14.app.feature.interview.interview.InterviewRecoveryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList", "ReturnCount", "TooManyFunctions")
class InterviewErrorViewModel
    @Inject
    constructor(
        private val getProgress: GetInterviewProgressUseCase,
        private val getManifest: GetInterviewMediaManifestUseCase,
        private val getResume: GetInterviewResumeUseCase,
        private val confirmResume: ConfirmInterviewResumeUseCase,
        private val abandonInterview: AbandonInterviewUseCase,
        private val submitAnswer: SubmitAnswerUseCase,
        private val savePendingAnswer: SavePendingInterviewAnswerUseCase,
        private val retainSessionForCleanup: RetainInterviewSessionForCleanupUseCase,
        private val recoveryStore: InterviewRecoveryStore,
    ) : MviViewModel<InterviewErrorIntent, InterviewErrorState, InterviewErrorEffect>(
            InterviewErrorState(),
        ) {
        private var sessionId: Long? = null

        override fun onIntent(intent: InterviewErrorIntent) {
            when (intent) {
                is InterviewErrorIntent.Load -> load(intent.errorType)
                InterviewErrorIntent.ClickAbort -> abort()
                InterviewErrorIntent.ClickResume -> resume()
            }
        }

        private fun load(errorType: InterviewErrorType) {
            reduce {
                copy(
                    errorType = errorType,
                    isLoading = errorType == InterviewErrorType.NETWORK,
                    canResume = errorType != InterviewErrorType.NETWORK,
                    canRetryAnswerSubmission = false,
                    failureMessage = null,
                )
            }
            viewModelScope.launch {
                val progress = getProgress()
                sessionId = progress?.sessionId
                if (progress == null) {
                    sendEffect(InterviewErrorEffect.InterviewAbandonCompleted)
                    return@launch
                }
                if (errorType == InterviewErrorType.SERVER_TEMPORARY) {
                    val canRetryAnswerSubmission =
                        getManifest(progress.sessionId)?.pendingAnswer != null
                    reduce {
                        copy(canRetryAnswerSubmission = canRetryAnswerSubmission)
                    }
                }
                if (errorType == InterviewErrorType.NETWORK) checkResume(progress.sessionId)
            }
        }

        private suspend fun checkResume(sessionId: Long) {
            getResume(sessionId)
                .onSuccess { status ->
                    when (status.resumeState) {
                        InterviewResumeState.Resumable -> {
                            reduce {
                                copy(
                                    isLoading = false,
                                    canResume = true,
                                    failureMessage = null,
                                )
                            }
                        }

                        InterviewResumeState.Ended -> {
                            handleTerminalStatus(status.status)
                        }

                        is InterviewResumeState.Unknown -> {
                            emitGlobal(GlobalAppEvent.ShowUnknownError)
                        }
                    }
                }.onFailure(::handleFailure)
        }

        private fun handleTerminalStatus(status: InterviewTerminalStatus?) {
            when (status) {
                InterviewTerminalStatus.Invalid -> {
                    reduce {
                        copy(
                            errorType = InterviewErrorType.STT,
                            isLoading = false,
                            canResume = false,
                        )
                    }
                }

                InterviewTerminalStatus.Completed,
                InterviewTerminalStatus.Abandoned,
                -> {
                    retainAndFinish(InterviewErrorEffect.InterviewAbandonCompleted)
                }

                is InterviewTerminalStatus.Unknown,
                null,
                -> {
                    emitGlobal(GlobalAppEvent.ShowUnknownError)
                }
            }
        }

        private fun resume() {
            if (state.value.isLoading) return
            val currentSessionId = sessionId ?: return
            if (state.value.errorType == InterviewErrorType.NETWORK && !state.value.canResume) {
                reduce { copy(isLoading = true) }
                viewModelScope.launch { checkResume(currentSessionId) }
                return
            }
            if (state.value.errorType == InterviewErrorType.SERVER_TEMPORARY &&
                !state.value.canRetryAnswerSubmission
            ) {
                reduce { copy(failureMessage = MISSING_PENDING_ANSWER_MESSAGE) }
                return
            }
            reduce { copy(isLoading = true, failureMessage = null) }
            when (state.value.errorType) {
                InterviewErrorType.NETWORK -> confirmNetworkResume(currentSessionId)

                InterviewErrorType.SERVER_TEMPORARY -> retryPendingAnswer(currentSessionId)

                InterviewErrorType.MIC_DEVICE,
                InterviewErrorType.STT,
                -> Unit
            }
        }

        private fun confirmNetworkResume(sessionId: Long) {
            viewModelScope.launch {
                confirmResume(sessionId)
                    .onSuccess { result ->
                        if (result.sessionEnded) {
                            if (result.status == InterviewTerminalStatus.Invalid ||
                                result.endType == InterviewEndType.SttReset
                            ) {
                                reduce {
                                    copy(
                                        errorType = InterviewErrorType.STT,
                                        isLoading = false,
                                        canResume = false,
                                    )
                                }
                            } else {
                                recoveryStore.publish(
                                    InterviewRecoveryStore.Result(
                                        nextQuestion = result.nextQuestion,
                                        sessionEnded = true,
                                        wrapUpMessage = result.wrapUpMessage,
                                        endType = result.endType,
                                        reportGenerating = false,
                                    ),
                                )
                                sendEffect(InterviewErrorEffect.InterviewResumeConfirmed)
                            }
                        } else {
                            val nextQuestion = result.nextQuestion
                            if (nextQuestion == null) {
                                emitGlobal(GlobalAppEvent.ShowUnknownError)
                            } else {
                                recoveryStore.publish(
                                    InterviewRecoveryStore.Result(
                                        nextQuestion = nextQuestion,
                                        sessionEnded = false,
                                        wrapUpMessage = null,
                                        endType = null,
                                        reportGenerating = false,
                                    ),
                                )
                                sendEffect(InterviewErrorEffect.InterviewResumeConfirmed)
                            }
                        }
                    }.onFailure(::handleFailure)
            }
        }

        private fun retryPendingAnswer(sessionId: Long) {
            viewModelScope.launch {
                val command = getManifest(sessionId)?.pendingAnswer
                if (command == null) {
                    reduce {
                        copy(
                            isLoading = false,
                            canRetryAnswerSubmission = false,
                            failureMessage = MISSING_PENDING_ANSWER_MESSAGE,
                        )
                    }
                    return@launch
                }
                submitAnswer(command)
                    .onSuccess { result ->
                        savePendingAnswer(null)
                        publishAnswerResult(result)
                        sendEffect(InterviewErrorEffect.AnswerSubmissionRecovered)
                    }.onFailure { error ->
                        when (error) {
                            is AiTemporarilyUnavailableException,
                            is NetworkUnavailableException,
                            -> {
                                reduce {
                                    copy(
                                        isLoading = false,
                                        canRetryAnswerSubmission = true,
                                        failureMessage = RETRY_LATER_MESSAGE,
                                    )
                                }
                            }

                            is ServerException -> {
                                emitGlobal(GlobalAppEvent.ShowServerErrorAndExit)
                            }

                            else -> {
                                emitGlobal(GlobalAppEvent.ShowUnknownError)
                            }
                        }
                    }
            }
        }

        private fun publishAnswerResult(result: SubmitAnswerResult) {
            recoveryStore.publish(
                InterviewRecoveryStore.Result(
                    nextQuestion = result.nextQuestion,
                    sessionEnded = result.sessionEnded,
                    wrapUpMessage = result.wrapUpMessage,
                    endType = result.endType,
                    reportGenerating = result.reportGenerating,
                ),
            )
        }

        private fun abort() {
            val currentSessionId = sessionId
            if (currentSessionId == null) {
                sendEffect(InterviewErrorEffect.InterviewAbandonCompleted)
                return
            }
            when (state.value.errorType) {
                InterviewErrorType.MIC_DEVICE -> {
                    retainAndFinish(InterviewErrorEffect.InterviewAbandonCompleted)
                }

                InterviewErrorType.STT -> {
                    retainAndFinish(InterviewErrorEffect.SttFailureAcknowledged)
                }

                InterviewErrorType.NETWORK -> {
                    abandon(currentSessionId, InterviewAbandonRequestCause.NetworkDisconnect)
                }

                InterviewErrorType.SERVER_TEMPORARY -> {
                    abandon(currentSessionId, InterviewAbandonRequestCause.UserExit)
                }
            }
        }

        private fun abandon(
            sessionId: Long,
            cause: InterviewAbandonRequestCause,
        ) {
            reduce { copy(isLoading = true) }
            viewModelScope.launch {
                abandonInterview(sessionId, cause)
                    .onSuccess { result ->
                        if (result.reportGenerating) {
                            recoveryStore.publish(
                                InterviewRecoveryStore.Result(
                                    nextQuestion = null,
                                    sessionEnded = true,
                                    wrapUpMessage = null,
                                    endType = null,
                                    reportGenerating = true,
                                    completionReason = InterviewCompletionReason.ABANDONED,
                                ),
                            )
                            sendEffect(InterviewErrorEffect.InterviewResumeConfirmed)
                        } else {
                            retainAndFinish(InterviewErrorEffect.InterviewAbandonCompleted)
                        }
                    }.onFailure { error ->
                        if (error is InterviewSessionAlreadyEndedException) {
                            retainAndFinish(InterviewErrorEffect.InterviewAbandonCompleted)
                        } else {
                            handleFailure(error)
                        }
                    }
            }
        }

        private fun retainAndFinish(effect: InterviewErrorEffect) {
            val currentSessionId = sessionId
            viewModelScope.launch {
                if (currentSessionId != null) retainSessionForCleanup(currentSessionId)
                sendEffect(effect)
            }
        }

        private fun handleFailure(error: Throwable) {
            when (error) {
                is NetworkUnavailableException -> {
                    reduce {
                        copy(
                            isLoading = false,
                            canResume = false,
                            failureMessage = NETWORK_RETRY_MESSAGE,
                        )
                    }
                }

                is ServerException -> {
                    emitGlobal(GlobalAppEvent.ShowServerErrorAndExit)
                }

                else -> {
                    emitGlobal(GlobalAppEvent.ShowUnknownError)
                }
            }
        }

        private fun emitGlobal(event: GlobalAppEvent) {
            if (event == GlobalAppEvent.ShowUnknownError) {
                reduce { copy(isLoading = false) }
            }

            viewModelScope.launch { GlobalErrorHandler.emit(event) }
        }

        private companion object {
            const val NETWORK_RETRY_MESSAGE = "네트워크 연결을 확인한 뒤 다시 시도해주세요."
            const val RETRY_LATER_MESSAGE = "잠시 후 같은 답변을 다시 시도해주세요."
            const val MISSING_PENDING_ANSWER_MESSAGE = "재시도할 답변 정보를 찾지 못했어요."
        }
    }

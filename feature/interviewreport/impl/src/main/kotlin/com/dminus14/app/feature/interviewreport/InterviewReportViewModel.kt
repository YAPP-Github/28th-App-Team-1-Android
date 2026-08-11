package com.dminus14.app.feature.interviewreport

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.usecase.ObserveInterviewReportUseCase
import com.dminus14.app.feature.interviewreport.mapper.InterviewReportUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InterviewReportViewModel
    @Inject
    constructor(
        private val observeInterviewReport: ObserveInterviewReportUseCase,
    ) : MviViewModel<InterviewReportIntent, InterviewReportState, InterviewReportEffect>(
            InterviewReportState(),
        ) {
        private var pollingJob: Job? = null

        /** Screen 이 route args 로 넘겨준 sessionId 를 State 에 확정한다. 최초 1회만 유효. */
        fun bindSessionId(sessionId: Long) {
            if (state.value.sessionId != 0L) return
            reduce { copy(sessionId = sessionId) }
        }

        override fun onIntent(intent: InterviewReportIntent) {
            when (intent) {
                InterviewReportIntent.Load -> {
                    load()
                }

                InterviewReportIntent.ClickRetry -> {
                    load()
                }

                InterviewReportIntent.ClickClose -> {
                    sendEffect(InterviewReportEffect.NavigateBack)
                }

                InterviewReportIntent.ClickWatchVideo -> {
                    handleWatchVideo(startSec = null)
                }

                is InterviewReportIntent.ClickWatchScene -> {
                    handleWatchVideo(startSec = intent.startSec)
                }

                is InterviewReportIntent.ClickHighlight -> {
                    reduce {
                        copy(
                            selectedHighlight =
                                InterviewReportState.SelectedHighlight(
                                    intent.cardIndex,
                                    intent.spanIndex,
                                ),
                        )
                    }
                }

                InterviewReportIntent.DismissHighlight -> {
                    reduce { copy(selectedHighlight = null) }
                }

                InterviewReportIntent.ClickGuestFeedbackInvite -> {
                    val sessionId = state.value.sessionId
                    if (sessionId > 0L) {
                        sendEffect(InterviewReportEffect.NavigateToGuestFeedback(sessionId))
                    }
                }
            }
        }

        private fun load() {
            val sessionId = state.value.sessionId
            if (sessionId <= 0L) return
            pollingJob?.cancel()
            reduce { copy(phase = InterviewReportState.Phase.Loading) }
            pollingJob =
                observeInterviewReport(sessionId)
                    .onEach { report ->
                        val phase =
                            when (report.status) {
                                InterviewReportStatus.READY -> {
                                    InterviewReportState.Phase.Ready(
                                        report = InterviewReportUiMapper.map(report),
                                    )
                                }

                                InterviewReportStatus.INSUFFICIENT_ANALYSIS -> {
                                    InterviewReportState.Phase.InsufficientAnalysis
                                }

                                InterviewReportStatus.FAILED -> {
                                    InterviewReportState.Phase.Failed
                                }

                                InterviewReportStatus.GENERATING,
                                InterviewReportStatus.UNKNOWN,
                                -> {
                                    InterviewReportState.Phase.Loading
                                }
                            }
                        reduce { copy(phase = phase) }
                    }.catch {
                        reduce { copy(phase = InterviewReportState.Phase.Failed) }
                    }.launchIn(viewModelScope)
        }

        private fun handleWatchVideo(startSec: Float?) {
            val current = state.value
            val ready = current.phase as? InterviewReportState.Phase.Ready ?: return
            val video = ready.report.video
            if (video == null || video.expired || video.url.isNullOrBlank()) {
                sendEffect(InterviewReportEffect.ShowToast("영상이 만료되어 재생할 수 없어요."))
                return
            }
            sendEffect(InterviewReportEffect.NavigateToPlayer(current.sessionId, startSec))
        }
    }

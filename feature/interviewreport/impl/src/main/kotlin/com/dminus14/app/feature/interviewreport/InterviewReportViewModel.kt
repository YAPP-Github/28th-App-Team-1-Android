package com.dminus14.app.feature.interviewreport

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.usecase.GetVideoExpiryUseCase
import com.dminus14.app.domain.usecase.ObserveInterviewReportUseCase
import com.dminus14.app.feature.interviewreport.mapper.InterviewReportUiMapper
import com.dminus14.app.feature.interviewreport.model.ReportUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewReportViewModel
    @Inject
    constructor(
        private val observeInterviewReport: ObserveInterviewReportUseCase,
        private val getVideoExpiry: GetVideoExpiryUseCase,
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

                is InterviewReportIntent.SelectCard -> {
                    reduce { copy(selectedCardIndex = intent.cardIndex) }
                }

                is InterviewReportIntent.SelectGuestFeedback -> {
                    reduce { copy(selectedGuestFeedbackIndex = intent.guestIndex) }
                }
            }
        }

        private fun load() {
            val sessionId = state.value.sessionId
            if (sessionId <= 0L) return
            pollingJob?.cancel()
            reduce {
                copy(
                    phase = InterviewReportState.Phase.Loading,
                    selectedCardIndex = 0,
                    selectedGuestFeedbackIndex = 0,
                    videoExpirySeconds = null,
                )
            }
            fetchVideoExpiry(sessionId)
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
                                    InterviewReportState.Phase.InsufficientAnalysis(
                                        report = InterviewReportUiMapper.map(report),
                                    )
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
                    }.catch { error ->
                        handleCommonError(error)
                    }.launchIn(viewModelScope)
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleCommonError(error: Throwable) {
            reduce { copy(phase = InterviewReportState.Phase.Failed) }
            when {
                error is NetworkUnavailableException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                }

                error is ServerException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                }

                else -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
                }
            }
        }

        /**
         * 카운트다운 시드용 잔여시간을 별도 조회한다. 리포트 폴링과 독립적이며, 실패하거나 이미
         * 만료됐으면 [InterviewReportState.videoExpirySeconds] 를 null 로 두어 카운트다운을 숨긴다.
         */
        private fun fetchVideoExpiry(sessionId: Long) {
            viewModelScope.launch {
                getVideoExpiry(sessionId)
                    .onSuccess { expiry ->
                        val seconds = expiry.expiresInSeconds.takeIf { !expiry.expired && it > 0L }
                        reduce { copy(videoExpirySeconds = seconds) }
                    }
            }
        }

        private fun handleWatchVideo(startSec: Float?) {
            val current = state.value
            val report = current.phase.reportOrNull() ?: return
            val video = report.video
            if (video == null || video.expired || video.url.isNullOrBlank()) {
                sendEffect(InterviewReportEffect.ShowToast("영상이 만료되어 재생할 수 없어요."))
                return
            }
            sendEffect(InterviewReportEffect.NavigateToPlayer(current.sessionId, startSec))
        }

        /**
         * Ready / InsufficientAnalysis 두 Phase 가 담은 리포트를 꺼낸다. 그 외에는 null.
         * 분석 부족(INSUFFICIENT_ANALYSIS) 상태에서도 영상 자체는 재생 가능할 수 있어,
         * Ready 로만 좁히면 [handleWatchVideo] 가 조용히 아무 반응도 하지 않게 된다.
         */
        private fun InterviewReportState.Phase.reportOrNull(): ReportUiModel? =
            when (this) {
                is InterviewReportState.Phase.Ready -> report
                is InterviewReportState.Phase.InsufficientAnalysis -> report
                else -> null
            }
    }

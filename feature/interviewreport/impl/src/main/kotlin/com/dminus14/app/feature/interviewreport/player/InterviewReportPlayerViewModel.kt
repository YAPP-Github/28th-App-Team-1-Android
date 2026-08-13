package com.dminus14.app.feature.interviewreport.player

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.ScriptRole
import com.dminus14.app.domain.usecase.GetInterviewReportUseCase
import com.dminus14.app.feature.interviewreport.mapper.InterviewReportUiMapper
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.app.feature.interviewreport.model.PlayerContentUiModel
import com.dminus14.app.feature.interviewreport.model.PlayerHighlightRefUiModel
import com.dminus14.app.feature.interviewreport.model.PlayerScriptLineUiModel
import com.dminus14.app.feature.interviewreport.model.PlayerSegmentUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MILLIS_PER_SECOND = 1000f

@HiltViewModel
class InterviewReportPlayerViewModel
    @Inject
    constructor(
        private val getInterviewReport: GetInterviewReportUseCase,
    ) : MviViewModel<
            InterviewReportPlayerIntent,
            InterviewReportPlayerState,
            InterviewReportPlayerEffect,
        >(
            InterviewReportPlayerState(),
        ) {
        private var sessionId: Long = 0L

        /** Screen 이 route args 로 넘겨준 sessionId 를 확정한다. 최초 1회만 유효. */
        fun bindSessionId(sessionId: Long) {
            if (this.sessionId == 0L) this.sessionId = sessionId
        }

        override fun onIntent(intent: InterviewReportPlayerIntent) {
            when (intent) {
                InterviewReportPlayerIntent.Load -> {
                    load()
                }

                InterviewReportPlayerIntent.ClickClose -> {
                    sendEffect(InterviewReportPlayerEffect.NavigateBack)
                }

                InterviewReportPlayerIntent.ToggleTranscript -> {
                    reduce { copy(transcriptVisible = !transcriptVisible) }
                }
            }
        }

        private fun load() {
            if (sessionId <= 0L) {
                // 초기 phase 가 Loading 이라, 여기서 조용히 빠져나가면 잘못된 route 인자로 진입한
                // 사용자가 인디케이터만 보는 화면에 갇힌다(닫기 아이콘은 PlayerReady 안에만 있어
                // 시스템 백 외에는 나갈 수단이 없음). Failed 로 보내 탭으로 닫을 수 있게 한다.
                reduce { copy(phase = InterviewReportPlayerState.Phase.Failed) }
                return
            }
            reduce { copy(phase = InterviewReportPlayerState.Phase.Loading) }
            viewModelScope.launch {
                getInterviewReport(sessionId)
                    .onSuccess { report ->
                        reduce {
                            copy(
                                phase =
                                    InterviewReportPlayerState.Phase.Ready(
                                        report.toPlayerContent(),
                                    ),
                            )
                        }
                    }.onFailure { error ->
                        handleCommonError(error)
                    }
            }
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleCommonError(error: Throwable) {
            reduce { copy(phase = InterviewReportPlayerState.Phase.Failed) }
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

        private fun InterviewReport.toPlayerContent(): PlayerContentUiModel {
            val sortedCards =
                cards.orEmpty().sortedWith(compareBy({ it.axisOrder }, { it.depthLevel }))
            val segments =
                sortedCards.mapNotNull { card ->
                    val starts = card.scriptSegments.orEmpty().mapNotNull { it.startSec }
                    val ends = card.scriptSegments.orEmpty().mapNotNull { it.endSec }
                    if (starts.isEmpty() || ends.isEmpty()) return@mapNotNull null
                    PlayerSegmentUiModel(
                        label = "질문 ${card.axisOrder}-${card.depthLevel}",
                        startMs = starts.min().toMillis(),
                        endMs = ends.max().toMillis(),
                    )
                }

            // 카드별 하이라이트를 시작 시각(ms) 기준으로 펼쳐서, 그 시각을 포함하는 script 라인에
            // 매칭한다. InterviewReportUiMapper 를 재사용해 톤·분석 등 UI 표시용 값을 그대로 쓴다
            // (카드 정렬 기준이 같아 sortedCards 와 인덱스가 대응된다).
            val uiCards: List<CardUiModel> = InterviewReportUiMapper.map(this).cards
            val highlightMarkers: List<Pair<Long, PlayerHighlightRefUiModel>> =
                uiCards.flatMap { card ->
                    card.highlights.mapNotNull { highlight ->
                        highlight.startSec?.let { startSec ->
                            startSec.toMillis() to
                                PlayerHighlightRefUiModel(
                                    highlight = highlight,
                                    cardTranscript = card.transcript,
                                    cardRedFlagNotices = card.cardRedFlagNotices,
                                )
                        }
                    }
                }

            val scriptLines =
                script.orEmpty().map { line ->
                    val startMs = line.startSec.toMillis()
                    val endMs = line.endSec.toMillis()
                    PlayerScriptLineUiModel(
                        isInterviewer = line.role == ScriptRole.INTERVIEWER,
                        text = line.text,
                        startMs = startMs,
                        endMs = endMs,
                        highlightRef =
                            highlightMarkers
                                .firstOrNull { (markerMs, _) -> markerMs in startMs until endMs }
                                ?.second,
                    )
                }
            return PlayerContentUiModel(
                videoUrl = video?.url,
                segments = segments,
                scriptLines = scriptLines,
            )
        }

        private fun Float.toMillis(): Long = (this * MILLIS_PER_SECOND).toLong()
    }

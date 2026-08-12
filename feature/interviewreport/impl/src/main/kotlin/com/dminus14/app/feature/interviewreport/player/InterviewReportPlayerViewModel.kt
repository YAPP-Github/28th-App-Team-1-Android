package com.dminus14.app.feature.interviewreport.player

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.ScriptRole
import com.dminus14.app.domain.usecase.GetInterviewReportUseCase
import com.dminus14.app.feature.interviewreport.model.PlayerContentUiModel
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
            if (sessionId <= 0L) return
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
                    }.onFailure {
                        reduce { copy(phase = InterviewReportPlayerState.Phase.Failed) }
                    }
            }
        }

        private fun InterviewReport.toPlayerContent(): PlayerContentUiModel {
            val segments =
                cards
                    .orEmpty()
                    .sortedWith(compareBy({ it.axisOrder }, { it.depthLevel }))
                    .mapNotNull { card ->
                        val starts = card.scriptSegments.orEmpty().mapNotNull { it.startSec }
                        val ends = card.scriptSegments.orEmpty().mapNotNull { it.endSec }
                        if (starts.isEmpty() || ends.isEmpty()) return@mapNotNull null
                        PlayerSegmentUiModel(
                            label = "질문 ${card.axisOrder}-${card.depthLevel}",
                            startMs = starts.min().toMillis(),
                            endMs = ends.max().toMillis(),
                        )
                    }
            val scriptLines =
                script.orEmpty().map { line ->
                    PlayerScriptLineUiModel(
                        isInterviewer = line.role == ScriptRole.INTERVIEWER,
                        text = line.text,
                        startMs = line.startSec.toMillis(),
                        endMs = line.endSec.toMillis(),
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

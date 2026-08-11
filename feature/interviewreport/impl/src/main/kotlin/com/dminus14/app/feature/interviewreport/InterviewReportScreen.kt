@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator

/**
 * 1차 리포트 화면 skeleton. C3 커밋에서 실제 콘텐츠를 채운다.
 */
@Composable
fun InterviewReportScreen(
    onNavigateBack: () -> Unit,
    onWatchVideo: (sessionId: Long, startSec: Float?) -> Unit,
    onOpenGuestFeedback: (sessionId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterviewReportViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(InterviewReportIntent.Load)
        viewModel.effect.collect { effect ->
            when (effect) {
                InterviewReportEffect.NavigateBack -> onNavigateBack()
                is InterviewReportEffect.NavigateToPlayer ->
                    onWatchVideo(effect.sessionId, effect.startSec)
                is InterviewReportEffect.NavigateToGuestFeedback ->
                    onOpenGuestFeedback(effect.sessionId)
                is InterviewReportEffect.ShowToast -> Unit // C3에서 Snackbar/Toast 처리.
            }
        }
    }

    InterviewReportContent(
        state = state,
        modifier = modifier,
    )
}

@Composable
internal fun InterviewReportContent(
    state: InterviewReportState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // C3 커밋에서 상태별 컨텐츠(Loading/Ready/InsufficientAnalysis/Failed)를 채운다.
        when (state.phase) {
            InterviewReportState.Phase.Loading -> HilitLoadingIndicator()
            InterviewReportState.Phase.Failed,
            InterviewReportState.Phase.InsufficientAnalysis,
            is InterviewReportState.Phase.Ready,
            -> HilitLoadingIndicator()
        }
    }
}

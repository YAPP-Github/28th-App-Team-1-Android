@file:Suppress("TooManyFunctions", "LongMethod", "CyclomaticComplexMethod")

package com.dminus14.app.feature.interview.interview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.interview.layer.InterviewScreenBaseLayer
import com.dminus14.app.feature.interview.layer.InterviewScreenOngoingLayer
import com.dminus14.app.feature.interview.layer.InterviewScreenPrepareLayer
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun InterviewScreen(
    modifier: Modifier = Modifier,
    viewModel: InterviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    InterviewContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
fun InterviewContent(
    state: InterviewState,
    onIntent: (InterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        InterviewScreenBaseLayer(modifier = Modifier.fillMaxSize())

        when (state.interviewScreenState) {
            InterviewScreenState.PREPARING,
            InterviewScreenState.ALMOST_PREPARED,
            InterviewScreenState.PREPARED,
            -> {
                InterviewScreenPrepareLayer(
                    isReady = state.isInterviewReady,
                    isPermissionGranted = true,
                    interviewScreenState = state.interviewScreenState,
                    onInterviewStart = { onIntent(InterviewIntent.StartInterview) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                InterviewScreenOngoingLayer(
                    interviewSpeaker = state.speaker,
                    remainingSeconds = state.remainingSeconds,
                    canFinishedEarly = state.canFinishedEarly,
                    isInterviewOngoing = state.isInterviewOngoing,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun InterviewScreenPreparingPreview() {
    HilitTheme {
        InterviewContent(
            state = InterviewState(),
            onIntent = {},
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun InterviewScreenPreparedPreview() {
    HilitTheme {
        InterviewContent(
            state =
                InterviewState(
                    interviewScreenState = InterviewScreenState.PREPARED,
                    isInterviewReady = true,
                ),
            onIntent = {},
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun InterviewScreenOngoingPreview() {
    HilitTheme {
        InterviewContent(
            state =
                InterviewState(
                    interviewScreenState = InterviewScreenState.ONGOING,
                    elapsedSeconds = 72,
                    isInterviewReady = true,
                ),
            onIntent = {},
        )
    }
}

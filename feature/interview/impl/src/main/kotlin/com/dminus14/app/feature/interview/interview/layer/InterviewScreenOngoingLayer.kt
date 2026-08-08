package com.dminus14.app.feature.interview.interview.layer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interview.component.InterviewSpeakerIndicator
import com.dminus14.app.feature.interview.component.InterviewTimer
import com.dminus14.app.feature.interview.interview.InterviewSpeaker
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.theme.HilitTheme

@Suppress("LongParameterList")
@Composable
fun InterviewScreenOngoingLayer(
    interviewSpeaker: InterviewSpeaker,
    remainingSeconds: Int,
    canFinishedEarly: Boolean,
    isInterviewOngoing: Boolean,
    modifier: Modifier = Modifier,
    onFinishRequest: () -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        // Timer
        InterviewTimer(
            remainingSeconds = remainingSeconds,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 39.dp),
        )

        // Indicator
        if (isInterviewOngoing) {
            InterviewSpeakerIndicator(
                speaker = interviewSpeaker,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 86.dp),
            )
        }

        if (canFinishedEarly) {
            HilitMiniButton(
                color = HilitMiniButtonColor.Dark,
                onClick = onFinishRequest,
                enabled = isInterviewOngoing,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 10.dp),
            ) {
                Text(text = "면접 종료하기")
            }
        }
    }
}

@Preview(name = "시간이 충분하고 AI가 질문 중")
@Composable
private fun InterviewScreenOngoingLayerAIPreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenOngoingLayer(
                interviewSpeaker = InterviewSpeaker.AI,
                remainingSeconds = 80,
                isInterviewOngoing = true,
                canFinishedEarly = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(name = "시간이 충분하고 사용자가 답변 중")
@Composable
private fun InterviewScreenOngoingLayerUserPreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenOngoingLayer(
                interviewSpeaker = InterviewSpeaker.User,
                remainingSeconds = 80,
                isInterviewOngoing = true,
                canFinishedEarly = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(name = "시간이 부족하고 사용자가 답변 중")
@Composable
private fun InterviewScreenOngoingLayerNotEnoughTimePreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenOngoingLayer(
                interviewSpeaker = InterviewSpeaker.User,
                remainingSeconds = 11,
                isInterviewOngoing = true,
                canFinishedEarly = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(name = "시간이 12분을 넘어 면접 종료")
@Composable
private fun InterviewScreenOngoingLayerNoTimePreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenOngoingLayer(
                interviewSpeaker = InterviewSpeaker.User,
                remainingSeconds = -11,
                isInterviewOngoing = false,
                canFinishedEarly = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

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
import com.dminus14.app.feature.interview.InterviewConstants
import com.dminus14.app.feature.interview.component.InterviewSpeakerIndicator
import com.dminus14.app.feature.interview.component.InterviewTimer
import com.dminus14.app.feature.interview.interview.InterviewScreenState
import com.dminus14.app.feature.interview.interview.InterviewSpeaker
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.theme.HilitTheme

@Suppress("LongMethod", "LongParameterList")
@Composable
fun InterviewScreenOngoingLayer(
    interviewSpeaker: InterviewSpeaker,
    screenState: InterviewScreenState,
    elapsedSeconds: Int,
    countdownSeconds: Int?,
    canFinishEarly: Boolean,
    hasSpeechStarted: Boolean,
    isQuestionAudioRetryVisible: Boolean,
    modifier: Modifier = Modifier,
    onFinishRequest: () -> Unit = {},
    onFinishAnswer: () -> Unit = {},
    onRetryQuestion: () -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        // Timer
        InterviewTimer(
            elapsedSeconds = elapsedSeconds,
            countdownSeconds = countdownSeconds,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 39.dp),
        )

        if (canFinishEarly && countdownSeconds == null) {
            val remainingSeconds =
                (
                    InterviewConstants.MAX_INTERVIEW_SECONDS - elapsedSeconds
                ).coerceAtLeast(0)
            Text(
                text =
                    "남은 시간 ${remainingSeconds / 60}:" +
                        (remainingSeconds % 60).toString().padStart(2, '0'),
                color = HilitTheme.colors.hilitWhite,
                style = HilitTheme.typography.body9,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 66.dp),
            )
        }

        // Indicator
        if (screenState == InterviewScreenState.QUESTION_PLAYING ||
            screenState == InterviewScreenState.ANSWER_RECORDING
        ) {
            InterviewSpeakerIndicator(
                speaker = interviewSpeaker,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 86.dp),
            )
        }

        if (screenState == InterviewScreenState.ANSWER_SUBMITTING ||
            screenState == InterviewScreenState.FINISHING
        ) {
            Text(
                text =
                    if (screenState == InterviewScreenState.FINISHING) {
                        "면접을 마무리하고 있어요"
                    } else {
                        "답변을 정리하고 있어요"
                    },
                color = HilitTheme.colors.hilitWhite,
                style = HilitTheme.typography.body2,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 86.dp),
            )
        }

        if (isQuestionAudioRetryVisible) {
            HilitMiniButton(
                color = HilitMiniButtonColor.Dark,
                onClick = onRetryQuestion,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 42.dp),
            ) {
                Text(text = "다시 시도")
            }
        }

        if (screenState == InterviewScreenState.FINISHING) {
            Text(
                text = "영상은 기본적으로 Wi-Fi 연결 시 업로드되며, 면접 세션 생성 후 24시간 안에 업로드되지 않으면 자동으로 삭제돼요.",
                color = HilitTheme.colors.hilitWhite,
                style = HilitTheme.typography.body9,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 28.dp, vertical = 42.dp),
            )
        }

        if (screenState == InterviewScreenState.ANSWER_RECORDING && hasSpeechStarted) {
            HilitMiniButton(
                color = HilitMiniButtonColor.Dark,
                onClick = onFinishAnswer,
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomStart,
                        ).padding(start = 20.dp, bottom = 10.dp),
            ) {
                Text(text = "답변 끝내기")
            }
        }

        if (canFinishEarly && screenState != InterviewScreenState.FINISHING) {
            HilitMiniButton(
                color = HilitMiniButtonColor.Dark,
                onClick = onFinishRequest,
                enabled = screenState != InterviewScreenState.ANSWER_SUBMITTING,
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
                screenState = InterviewScreenState.QUESTION_PLAYING,
                elapsedSeconds = 80,
                countdownSeconds = null,
                canFinishEarly = false,
                hasSpeechStarted = false,
                isQuestionAudioRetryVisible = false,
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
                interviewSpeaker = InterviewSpeaker.USER,
                screenState = InterviewScreenState.ANSWER_RECORDING,
                elapsedSeconds = 80,
                countdownSeconds = null,
                canFinishEarly = false,
                hasSpeechStarted = true,
                isQuestionAudioRetryVisible = false,
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
                interviewSpeaker = InterviewSpeaker.USER,
                screenState = InterviewScreenState.ANSWER_RECORDING,
                elapsedSeconds = 711,
                countdownSeconds = 9,
                canFinishEarly = true,
                hasSpeechStarted = true,
                isQuestionAudioRetryVisible = false,
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
                interviewSpeaker = InterviewSpeaker.USER,
                screenState = InterviewScreenState.FINISHING,
                elapsedSeconds = 720,
                countdownSeconds = 0,
                canFinishEarly = true,
                hasSpeechStarted = false,
                isQuestionAudioRetryVisible = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

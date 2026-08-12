package com.dminus14.app.feature.interview.interview.layer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interview.component.InterviewReadinessIndicator
import com.dminus14.app.feature.interview.component.InterviewStartButton
import com.dminus14.app.feature.interview.interview.InterviewScreenState
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

@Suppress("LongMethod", "LongParameterList")
@Composable
fun InterviewScreenPrepareLayer(
    isReady: Boolean,
    isPermissionGranted: Boolean,
    interviewScreenState: InterviewScreenState,
    showOpenSettings: Boolean,
    hasEnoughStorage: Boolean,
    onInterviewStart: () -> Unit,
    onPermissionDeniedBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val preparingText = buildAnnotatedString { append("면접을 준비하고 있어요") }
    val soundGuideText =
        buildAnnotatedString {
            append("질문은 ")
            withHilitTextHighlight { append("소리") }
            append("로만 나와요")
        }
    /*
    val timeGuideText =
        buildAnnotatedString {
            append("면접은 총 ")
            withHilitTextHighlight { append("10분") }
            append("으로 진행돼요")
        }
     */
    val titleText: AnnotatedString =
        if (interviewScreenState == InterviewScreenState.START_GUIDE) {
            soundGuideText
        } else {
            preparingText
        }

    Box(
        modifier = modifier,
    ) {
        // Title Text
        HilitText(
            text = titleText,
            style = getTitleTextStyle(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 74.dp),
        )

        // Loading Indicator
        if (interviewScreenState == InterviewScreenState.DEVICE_CHECK ||
            interviewScreenState == InterviewScreenState.QUESTION_PREPARING
        ) {
            InterviewReadinessIndicator(
                isReady = isReady,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 94.dp),
            )
        }

        // Back Button
        if (!isPermissionGranted) {
            HilitIcon(
                asset = HilitIconAsset.Left,
                contentDescription = "Go back",
                tint = HilitTheme.colors.hilitWhite,
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .clickable(onClick = onPermissionDeniedBack)
                        .padding(start = 20.dp, top = 10.dp),
            )
        }

        // Bottom Button
        if (showOpenSettings) {
            Text(
                text = "설정에서 권한 허용하기",
                color = HilitTheme.colors.hilitWhite,
                style = HilitTheme.typography.body4,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 154.dp)
                        .clickable(onClick = onOpenSettings),
            )
        } else if (!hasEnoughStorage && interviewScreenState != InterviewScreenState.START_GUIDE) {
            Text(
                text = "저장 공간이 부족해요",
                color = HilitTheme.colors.hilitWhite,
                style = HilitTheme.typography.body4,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 154.dp),
            )
        }

        if (interviewScreenState == InterviewScreenState.START_GUIDE) {
            InterviewStartButton(
                isReady = isReady,
                onClick = onInterviewStart,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun getTitleTextStyle(): TextStyle =
    HilitTheme.typography.head3.copy(
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = HilitTheme.colors.hilitWhite,
    )

@Preview
@Composable
private fun InterviewScreenPreparingLayerPreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenPrepareLayer(
                isReady = false,
                isPermissionGranted = true,
                interviewScreenState = InterviewScreenState.DEVICE_CHECK,
                showOpenSettings = false,
                hasEnoughStorage = true,
                onInterviewStart = {},
                onPermissionDeniedBack = {},
                onOpenSettings = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun InterviewScreenPrepareLayerAlmostPreparedPreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenPrepareLayer(
                isReady = true,
                isPermissionGranted = true,
                interviewScreenState = InterviewScreenState.QUESTION_PREPARING,
                showOpenSettings = false,
                hasEnoughStorage = true,
                onInterviewStart = {},
                onPermissionDeniedBack = {},
                onOpenSettings = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun InterviewScreenPrepareLayerPermissionNotGrantedPreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenPrepareLayer(
                isReady = true,
                isPermissionGranted = false,
                interviewScreenState = InterviewScreenState.DEVICE_CHECK,
                showOpenSettings = true,
                hasEnoughStorage = true,
                onInterviewStart = {},
                onPermissionDeniedBack = {},
                onOpenSettings = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun InterviewScreenPrepareLayerPreparedPreview() {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = HilitTheme.colors.gray300),
        ) {
            InterviewScreenPrepareLayer(
                isReady = true,
                isPermissionGranted = true,
                interviewScreenState = InterviewScreenState.START_GUIDE,
                showOpenSettings = false,
                hasEnoughStorage = true,
                onInterviewStart = {},
                onPermissionDeniedBack = {},
                onOpenSettings = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

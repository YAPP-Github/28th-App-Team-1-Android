@file:Suppress("LongMethod")

package com.dminus14.app.feature.interview.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.interview.api.InterviewErrorType
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 면접 오류 화면 (Page 2).
 *
 * Figma Nodes:
 * - 609:9269 (마이크 상태 불량)
 * - 609:9259 (네트워크 상태 불량)
 */
@Composable
fun InterviewErrorScreen(
    errorType: InterviewErrorType,
    onNavigateHome: () -> Unit,
    onResumeInterview: () -> Unit,
    viewModel: InterviewErrorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(errorType) {
        viewModel.initErrorType(errorType)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                InterviewErrorEffect.NavigateToHome -> onNavigateHome()
                InterviewErrorEffect.ResumeInterview -> onResumeInterview()
            }
        }
    }

    InterviewErrorContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

/** ViewModel-free 오류 UI Content */
@Composable
fun InterviewErrorContent(
    state: InterviewErrorState,
    onIntent: (InterviewErrorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMicError = state.errorType == InterviewErrorType.MIC
    val subtitleText =
        if (isMicError) {
            "마이크 상태를 확인하고 조용한 곳에서\n" +
                "면접을 다시 시작해주세요."
        } else {
            "네트워크가 불안정해 면접을 이어갈 수 없어요.\n" +
                "연결을 확인하고 면접을 다시 시작해주세요."
        }
    val descriptionText =
        if (isMicError) {
            "면접을 중단해도 이용권은 차감되지 않아요"
        } else {
            "중단하기를 선택할 경우에, 이용권은 차감되지 않아요"
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite)
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .size(54.dp)
                        .background(color = HilitTheme.colors.hilitBlack800),
            ) {
                HilitIcon(
                    asset = if (isMicError) HilitIconAsset.Mic else HilitIconAsset.Network,
                    contentDescription = null,
                    tint = HilitTheme.colors.hilitGreen500,
                    modifier = Modifier.size(24.dp),
                )
                HilitIcon(
                    asset = HilitIconAsset.AlertRed,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HilitText(
                text =
                    buildAnnotatedString {
                        if (isMicError) {
                            withHilitTextHighlight { append("목소리") }
                            append("가 잘 들리지 않아요")
                        } else {
                            withHilitTextHighlight { append("연결") }
                            append("이 끊겼어요")
                        }
                    },
                highlightColor = HilitTextHighlightColor.Red,
                color = HilitTheme.colors.hilitBlack800,
                style = HilitTheme.typography.head3.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = subtitleText,
                style = HilitTheme.typography.body4,
                color = HilitTheme.colors.gray500,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(HilitTheme.colors.gray100)
                        .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Info,
                    contentDescription = null,
                    tint = HilitTheme.colors.hilitBlack800,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = descriptionText,
                    style = HilitTheme.typography.body9,
                    color = HilitTheme.colors.gray700,
                )
            }
        }

        if (isMicError) {
            HilitFixedBottomButton(
                text = "중단하기",
                type = HilitButtonType.Light,
                onClick = { onIntent(InterviewErrorIntent.ClickAbort) },
            )
        } else {
            HilitFixedBottomDualButton(
                leftText = "중단하기",
                rightText = "이어서 진행하기",
                type = HilitFixedBottomDualButtonType.Default,
                onLeftClick = { onIntent(InterviewErrorIntent.ClickAbort) },
                onRightClick = { onIntent(InterviewErrorIntent.ClickResume) },
            )
        }
    }
}

@Preview(name = "Interview Error Mic", widthDp = 375, heightDp = 812)
@Composable
private fun InterviewErrorMicPreview() {
    HilitTheme {
        InterviewErrorContent(
            state = InterviewErrorState(errorType = InterviewErrorType.MIC),
            onIntent = {},
        )
    }
}

@Preview(name = "Interview Error Network", widthDp = 375, heightDp = 812)
@Composable
private fun InterviewErrorNetworkPreview() {
    HilitTheme {
        InterviewErrorContent(
            state = InterviewErrorState(errorType = InterviewErrorType.NETWORK),
            onIntent = {},
        )
    }
}

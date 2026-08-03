package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.OnBoardingInterviewIntent
import com.dminus14.app.feature.onboarding.OnBoardingLoadingStepStatus
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val TopBarPadding = 24.dp
private val TitleToSubtitleSpacing = 8.dp
private val SubtitleToStepsSpacing = 32.dp
private val StepRowSpacing = 8.dp
private val DecorationHeight = 220.dp
private const val DECORATION_ROTATION_DEGREES = 6f

@Composable
fun OnBoardingPreloadStep(
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitBlack800),
    ) {
        PreloadDecoration(modifier = Modifier.align(Alignment.BottomCenter))

        val closeInteractionSource = remember { MutableInteractionSource() }
        HilitIcon(
            asset = HilitIconAsset.Cancel,
            contentDescription = null,
            tint = HilitTheme.colors.hilitWhite,
            modifier =
                Modifier
                    .padding(top = TopBarPadding, start = TopBarPadding)
                    .size(HilitIconAsset.Cancel.defaultSize)
                    .clickable(
                        interactionSource = closeInteractionSource,
                        indication = null,
                        onClick = { onIntent(OnBoardingInterviewIntent.ClickClose) },
                    ),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "최적의 면접 환경을\n준비하고 있어요",
                style = HilitTheme.typography.head3,
                color = HilitTheme.colors.hilitWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "잠시만 기다려주세요",
                style = HilitTheme.typography.sub8,
                color = HilitTheme.colors.gray300,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(top = TitleToSubtitleSpacing)
                        .fillMaxWidth(),
            )

            OnBoardingPreloadSteps(modifier = Modifier.padding(top = SubtitleToStepsSpacing))
        }
    }
}

@Composable
private fun OnBoardingPreloadSteps(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(StepRowSpacing),
    ) {
        OnBoardingLoadingStepRow(
            text = "기본정보 분석 중",
            status = OnBoardingLoadingStepStatus.InProgress,
        )
        OnBoardingLoadingStepRow(
            text = "채용 정보 분석 중",
            status = OnBoardingLoadingStepStatus.Waiting,
        )
        OnBoardingLoadingStepRow(
            text = "나의 포폴 분석 중",
            status = OnBoardingLoadingStepStatus.Waiting,
        )
    }
}

@Composable
private fun PreloadDecoration(modifier: Modifier = Modifier) {
    val gradient =
        Brush.linearGradient(
            colors = listOf(HilitTheme.colors.hilitGreen600, HilitTheme.colors.hilitGreen500),
            start = Offset(0f, 0f),
            end = Offset(1f, 1f),
        )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .size(DecorationHeight)
                .graphicsLayer(rotationZ = DECORATION_ROTATION_DEGREES)
                .background(gradient),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1B1F, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPreloadStepPreview() {
    HilitTheme {
        OnBoardingPreloadStep(onIntent = {})
    }
}

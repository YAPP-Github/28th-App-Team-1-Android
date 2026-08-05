package com.dminus14.app.feature.onboarding.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.OnBoardingInterviewIntent
import com.dminus14.app.feature.onboarding.OnBoardingLoadingStepStatus
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay

private val TopBarPadding = 24.dp
private val TitleToSubtitleSpacing = 8.dp
private val SubtitleToStepsSpacing = 32.dp
private val StepRowSpacing = 8.dp

// Figma 443:9769 — 뷰포트 상단 대비 좌측 상단 코너의 Y 비율(≈61%),
// 우측 상단은 조금 더 아래에 있어 대각선 상단 경계를 만든다.
private const val DECORATION_LEFT_TOP_RATIO = 0.61f
private const val DECORATION_RIGHT_TOP_RATIO = 0.72f

// Figma 그라디언트(189.47deg, #89E377 23% → #60D549 70%)를 근사한다.
private val DecorationGradientTop = Color(0xFF89E377)
private val DecorationGradientBottom = Color(0xFF60D549)

/**
 * 준비 화면에서 회전해서 노출할 카피 리스트. 최종 문구·순서·주기는 디자인/PM 확정 예정이며,
 * 여기 값은 우선 노출용 초안이다. (스펙 S2: "무한 스피너 + 철학 회전 문구")
 */
private val PreloadRotatingCopies =
    listOf(
        "잠시만 기다려주세요",
        "당신만을 위한 질문을 고르고 있어요",
        "면접관의 눈으로 포트폴리오를 다시 읽고 있어요",
        "긴장돼도 괜찮아요, 실전 감각을 위해 준비 중이에요",
    )
private const val PRELOAD_COPY_ROTATION_MS = 3_000L

@Composable
fun OnBoardingPreloadStep(
    basicInfoStatus: OnBoardingLoadingStepStatus,
    jdStatus: OnBoardingLoadingStepStatus,
    portfolioStatus: OnBoardingLoadingStepStatus,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitBlack800),
    ) {
        PreloadDecoration(modifier = Modifier.fillMaxSize())

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

            PreloadRotatingSubtitle(
                modifier =
                    Modifier
                        .padding(top = TitleToSubtitleSpacing)
                        .fillMaxWidth(),
            )

            OnBoardingPreloadSteps(
                basicInfoStatus = basicInfoStatus,
                jdStatus = jdStatus,
                portfolioStatus = portfolioStatus,
                modifier = Modifier.padding(top = SubtitleToStepsSpacing),
            )
        }
    }
}

/**
 * [PreloadRotatingCopies]를 순환하며 페이드 전환으로 노출한다.
 * 마지막 항목까지 가면 처음으로 돌아온다.
 */
@Composable
private fun PreloadRotatingSubtitle(modifier: Modifier = Modifier) {
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(PRELOAD_COPY_ROTATION_MS)
            index = (index + 1) % PreloadRotatingCopies.size
        }
    }
    AnimatedContent(
        targetState = index,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "onBoardingPreloadRotatingCopy",
        modifier = modifier,
    ) { current ->
        Text(
            text = PreloadRotatingCopies[current],
            style = HilitTheme.typography.sub8,
            color = HilitTheme.colors.gray300,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun OnBoardingPreloadSteps(
    basicInfoStatus: OnBoardingLoadingStepStatus,
    jdStatus: OnBoardingLoadingStepStatus,
    portfolioStatus: OnBoardingLoadingStepStatus,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(StepRowSpacing),
    ) {
        OnBoardingLoadingStepRow(
            text = "기본정보 분석 중",
            status = basicInfoStatus,
        )
        OnBoardingLoadingStepRow(
            text = "채용 정보 분석 중",
            status = jdStatus,
        )
        OnBoardingLoadingStepRow(
            text = "나의 포폴 분석 중",
            status = portfolioStatus,
        )
    }
}

/**
 * Preload 화면 하단의 초록색 대각선 사각형. Figma 443:9769 스펙에 맞춰 좌·우 상단
 * Y 위치를 개별 지정한 Path로 그린다. 이전 구현이 `Modifier.size().rotationZ`로
 * 정사각형을 회전시키다 폭이 잘리던 문제를 해결한다.
 */
@Composable
private fun PreloadDecoration(modifier: Modifier = Modifier) {
    val gradient =
        Brush.verticalGradient(
            colors = listOf(DecorationGradientTop, DecorationGradientBottom),
        )
    Canvas(modifier = modifier) {
        val leftTopY = size.height * DECORATION_LEFT_TOP_RATIO
        val rightTopY = size.height * DECORATION_RIGHT_TOP_RATIO
        val path =
            Path().apply {
                moveTo(0f, leftTopY)
                lineTo(size.width, rightTopY)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        drawPath(path = path, brush = gradient)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1B1F, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingPreloadStepPreview() {
    HilitTheme {
        OnBoardingPreloadStep(
            basicInfoStatus = OnBoardingLoadingStepStatus.Completed,
            jdStatus = OnBoardingLoadingStepStatus.InProgress,
            portfolioStatus = OnBoardingLoadingStepStatus.Waiting,
            onIntent = {},
        )
    }
}

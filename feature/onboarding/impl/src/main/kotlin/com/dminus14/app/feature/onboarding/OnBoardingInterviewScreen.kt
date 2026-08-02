package com.dminus14.app.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.onboarding.component.OnBoardingJobDescriptionStep
import com.dminus14.app.feature.onboarding.component.OnBoardingMainProjectStep
import com.dminus14.app.feature.onboarding.component.OnBoardingPortfolioStep
import com.dminus14.app.feature.onboarding.component.OnBoardingPreloadStep
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.progressbar.HilitProgressBar
import com.dminus14.designsystem.component.topbar.HilitIconTopBar
import com.dminus14.designsystem.component.topbar.HilitTextTopBar
import com.dminus14.designsystem.component.topbar.TopBarType
import com.dminus14.designsystem.theme.HilitTheme

private val ContentHorizontalPadding = 20.dp
private val ProgressBarHorizontalPadding = 20.dp
private val ProgressBarVerticalPadding = 4.dp
private const val PROGRESS_MAX_STEP = 3

@Suppress("UnusedParameter") // onNavigate: 인터뷰 완료 후 이동할 목적지가 정해지면 연결 예정
@Composable
fun OnBoardingInterviewScreen(
    onNavigate: (Any) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnBoardingInterviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(OnBoardingInterviewIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnBoardingInterviewEffect.CloseRequested -> onClose()
            }
        }
    }

    OnBoardingInterviewContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun OnBoardingInterviewContent(
    state: OnBoardingInterviewState,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.step == OnBoardingInterviewStep.Preload) {
        OnBoardingPreloadStep(onIntent = onIntent, modifier = modifier)
        return
    }

    BackHandler {
        onIntent(OnBoardingInterviewIntent.PreviousClick)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite),
    ) {
        OnBoardingInterviewHeader(
            step = state.step,
            onCloseClick = { onIntent(OnBoardingInterviewIntent.CloseClick) },
            onSkipClick = { onIntent(OnBoardingInterviewIntent.SkipClick) },
        )

        OnBoardingInterviewAnimatedStepContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        HilitFixedBottomDualButton(
            leftText = "이전으로",
            rightText = "계속하기",
            leftEnabled = true,
            rightEnabled = true,
            onLeftClick = { onIntent(OnBoardingInterviewIntent.PreviousClick) },
            onRightClick = { onIntent(OnBoardingInterviewIntent.ContinueClick) },
        )
    }
}

@Composable
private fun OnBoardingInterviewAnimatedStepContent(
    state: OnBoardingInterviewState,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = state.step,
        modifier = modifier,
        transitionSpec = {
            val forward = targetState.ordinal > initialState.ordinal
            if (forward) {
                (
                    slideInHorizontally { fullWidth -> fullWidth } + fadeIn()
                ) togetherWith (
                    slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                )
            } else {
                (
                    slideInHorizontally { fullWidth -> -fullWidth } + fadeIn()
                ) togetherWith (
                    slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
                )
            }
        },
        label = "onBoardingInterviewStep",
    ) { step ->
        OnBoardingInterviewStepContent(
            step = step,
            state = state,
            onIntent = onIntent,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = ContentHorizontalPadding),
        )
    }
}

@Composable
private fun OnBoardingInterviewHeader(
    step: OnBoardingInterviewStep,
    onCloseClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    when (step) {
        OnBoardingInterviewStep.Portfolio -> {
            HilitIconTopBar(
                type = TopBarType.HideRight,
                title = "",
                onLeftClick = onCloseClick,
            )
        }

        OnBoardingInterviewStep.JobDescription,
        OnBoardingInterviewStep.MainProject,
        OnBoardingInterviewStep.Preload,
        -> {
            HilitTextTopBar(
                type = TopBarType.HideMiddle,
                buttonText = "건너뛰기",
                onLeftClick = onCloseClick,
                onButtonClick = onSkipClick,
            )
        }
    }

    HilitProgressBar(
        step = step.toProgressStep(),
        maxStep = PROGRESS_MAX_STEP,
        modifier =
            Modifier
                .padding(horizontal = ProgressBarHorizontalPadding)
                .padding(vertical = ProgressBarVerticalPadding),
    )
}

@Composable
private fun OnBoardingInterviewStepContent(
    step: OnBoardingInterviewStep,
    state: OnBoardingInterviewState,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (step) {
        OnBoardingInterviewStep.JobDescription -> {
            OnBoardingJobDescriptionStep(
                tab = state.jobDescriptionTab,
                link = state.jobDescriptionLink,
                text = state.jobDescriptionText,
                onIntent = onIntent,
                modifier = modifier,
            )
        }

        OnBoardingInterviewStep.Portfolio -> {
            OnBoardingPortfolioStep(
                fileName = state.portfolioFileName,
                showExistingPortfolioModal = state.showExistingPortfolioModal,
                showRequiredError = state.showPortfolioRequiredError,
                onIntent = onIntent,
                modifier = modifier,
            )
        }

        OnBoardingInterviewStep.MainProject -> {
            OnBoardingMainProjectStep(
                text = state.mainProjectText,
                onIntent = onIntent,
                modifier = modifier,
            )
        }

        OnBoardingInterviewStep.Preload -> {
            Unit
        }
    }
}

private fun OnBoardingInterviewStep.toProgressStep(): Int =
    when (this) {
        OnBoardingInterviewStep.JobDescription -> 1

        OnBoardingInterviewStep.Portfolio -> 2

        OnBoardingInterviewStep.MainProject,
        OnBoardingInterviewStep.Preload,
        -> PROGRESS_MAX_STEP
    }

@Preview(name = "JobDescription", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingInterviewJobDescriptionPreview() {
    HilitTheme {
        OnBoardingInterviewContent(
            state = OnBoardingInterviewState(step = OnBoardingInterviewStep.JobDescription),
            onIntent = {},
        )
    }
}

@Preview(name = "Portfolio", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingInterviewPortfolioPreview() {
    HilitTheme {
        OnBoardingInterviewContent(
            state = OnBoardingInterviewState(
                step = OnBoardingInterviewStep.Portfolio,
                showExistingPortfolioModal=true),
            onIntent = {},
        )
    }
}

@Preview(name = "MainProject", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingInterviewMainProjectPreview() {
    HilitTheme {
        OnBoardingInterviewContent(
            state = OnBoardingInterviewState(step = OnBoardingInterviewStep.MainProject),
            onIntent = {},
        )
    }
}

@Preview(name = "Preload", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingInterviewPreloadPreview() {
    HilitTheme {
        OnBoardingInterviewContent(
            state = OnBoardingInterviewState(step = OnBoardingInterviewStep.Preload),
            onIntent = {},
        )
    }
}

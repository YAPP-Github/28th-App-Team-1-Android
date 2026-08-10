package com.dminus14.app.feature.onboarding

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.onboarding.component.OnBoardingJobDescriptionStep
import com.dminus14.app.feature.onboarding.component.OnBoardingMainProjectStep
import com.dminus14.app.feature.onboarding.component.OnBoardingPortfolioStep
import com.dminus14.app.feature.onboarding.component.OnBoardingPreloadStep
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalType
import com.dminus14.designsystem.component.progressbar.HilitProgressBar
import com.dminus14.designsystem.component.topbar.HilitIconTopBar
import com.dminus14.designsystem.component.topbar.HilitTextTopBar
import com.dminus14.designsystem.component.topbar.TopBarType
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val ContentHorizontalPadding = 20.dp
private val ProgressBarHorizontalPadding = 20.dp
private val ProgressBarVerticalPadding = 4.dp
private const val PROGRESS_MAX_STEP = 3
private const val MIME_PDF = "application/pdf"

@Composable
fun OnBoardingInterviewScreen(
    onNavigate: (Any) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnBoardingInterviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val portfolioPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            scope.launch {
                val picked = withContext(Dispatchers.IO) { copyPdfToCache(context, uri) }
                if (picked != null) {
                    viewModel.onIntent(
                        OnBoardingInterviewIntent.PortfolioFileSelected(
                            file = picked.file,
                            fileName = picked.name,
                        ),
                    )
                }
            }
        }

    LaunchedEffect(Unit) {
        viewModel.onIntent(OnBoardingInterviewIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnBoardingInterviewEffect.CloseRequested -> {
                    onClose()
                }

                OnBoardingInterviewEffect.LaunchPortfolioPicker -> {
                    portfolioPickerLauncher.launch(arrayOf(MIME_PDF))
                }

                is OnBoardingInterviewEffect.NavigateToResult -> {
                    onNavigate(effect.sessionId)
                }
            }
        }
    }

    OnBoardingInterviewContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

private data class PickedPdf(
    val file: File,
    val name: String,
)

/** SAF로 고른 PDF를 앱 캐시로 복사한다. 업로드 UseCase는 [File]을 요구하기 때문이다. */
private fun copyPdfToCache(
    context: Context,
    uri: Uri,
): PickedPdf? {
    val resolver = context.contentResolver
    val name = resolver.queryDisplayName(uri) ?: "portfolio.pdf"
    return runCatching {
        val target = File(context.cacheDir, name)
        val copied =
            resolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
                true
            } ?: false
        if (copied) PickedPdf(file = target, name = name) else null
    }.getOrNull()
}

private fun android.content.ContentResolver.queryDisplayName(uri: Uri): String? =
    query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
    }

@Suppress("UnusedParameter")
@Composable
private fun OnBoardingInterviewContent(
    state: OnBoardingInterviewState,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.step == OnBoardingInterviewStep.Preload) {
        OnBoardingPreloadStep(
            basicInfoStatus = state.loadingBasicInfo,
            jdStatus = state.loadingJd,
            portfolioStatus = state.loadingPortfolio,
            onIntent = onIntent,
            modifier = modifier,
        )
        // Preload 중 세션 생성 실패는 화면에 노출되지 않던 버그를 다이얼로그로 안내한다.
        // 확인 시 MainProject 스텝으로 되돌려 재시도 가능하게 만든다.
        if (state.errorMessage != null) {
            OnBoardingPreloadFailureDialog(
                message = state.errorMessage,
                onIntent = onIntent,
            )
        }
        return
    }

    BackHandler {
        onIntent(OnBoardingInterviewIntent.ClickPrevious)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite),
    ) {
        OnBoardingInterviewHeader(
            step = state.step,
            onCloseClick = { onIntent(OnBoardingInterviewIntent.ClickClose) },
            onSkipClick = { onIntent(OnBoardingInterviewIntent.ClickSkip) },
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
            rightEnabled = state.isContinueEnabled(),
            onLeftClick = { onIntent(OnBoardingInterviewIntent.ClickPrevious) },
            onRightClick = { onIntent(OnBoardingInterviewIntent.ClickContinue) },
        )
    }

    if (state.showRelevanceFailDialog) {
        OnBoardingRelevanceFailDialog(onIntent = onIntent)
    }
}

/**
 * Preload(세션 생성/준비) 실패 안내 다이얼로그. 그 외 실패는 Preload 화면 아래
 * 로딩 인디케이터 그대로였는데, 다이얼로그로 사용자에게 알리고 이전 스텝으로 복귀시킨다.
 */
@Composable
private fun OnBoardingPreloadFailureDialog(
    message: String,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
) {
    HilitModal(
        type = HilitModalType.InvisibleInfo,
        title = "면접 준비에 실패했어요",
        subtitle = message,
        dismissible = false,
        buttons = {
            HilitFixedBottomButton(
                text = "확인",
                onClick = {
                    onIntent(OnBoardingInterviewIntent.ClickPreloadFailureAcknowledged)
                },
            )
        },
    )
}

/**
 * S3.5 연관성 판단이 4회 연속 실패했을 때 뜨는 이스케이프 다이얼로그.
 * 스펙: 포트폴리오 다시 올리기 vs 집중 프로젝트 없이 진행.
 */
@Composable
private fun OnBoardingRelevanceFailDialog(onIntent: (OnBoardingInterviewIntent) -> Unit) {
    HilitModal(
        type = HilitModalType.InvisibleInfo,
        title = "포트폴리오에서\n그 내용을 계속 찾지 못했어요",
        subtitle = "포트폴리오를 다시 올리거나, 집중 프로젝트 없이 진행할 수 있어요.",
        dismissible = false,
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "집중 프로젝트 없이 진행",
                rightText = "포트폴리오 다시 올리기",
                onLeftClick = {
                    onIntent(OnBoardingInterviewIntent.ClickRelevanceProceedWithoutMainProject)
                },
                onRightClick = {
                    onIntent(OnBoardingInterviewIntent.ClickRelevanceRetryPortfolio)
                },
            )
        },
    )
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
                linkStatus = state.jdLinkStatus,
                linkSubText = state.jdLinkSubText,
                text = state.jobDescriptionText,
                onIntent = onIntent,
                modifier = modifier,
            )
        }

        OnBoardingInterviewStep.Portfolio -> {
            OnBoardingPortfolioStep(
                fileName = state.portfolioFileName,
                isProcessing = state.isPortfolioProcessing,
                uploadProgress = state.portfolioUploadProgress,
                showExistingPortfolioModal = state.showExistingPortfolioModal,
                errorMessage = state.portfolioErrorMessage,
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

/**
 * 계속하기 버튼 활성 여부를 스텝별로 파생한다.
 * - Portfolio: 업로드가 끝나 완료 카드(`PdfUploadType.Completed`)로 표시되는 상태와 같은 조건
 *   (`!isProcessing && fileName != null`)에서만 활성.
 * - 나머지 스텝: 현재 스펙상 항상 활성. 각 스텝 자체 검증은 [OnBoardingInterviewViewModel.onIntent]
 *   가 Continue 처리 안에서 담당한다.
 */
private fun OnBoardingInterviewState.isContinueEnabled(): Boolean =
    when (step) {
        OnBoardingInterviewStep.Portfolio ->
            !isPortfolioProcessing && portfolioFileName != null

        OnBoardingInterviewStep.JobDescription,
        OnBoardingInterviewStep.MainProject,
        OnBoardingInterviewStep.Preload,
        -> true
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
            state =
                OnBoardingInterviewState(
                    step = OnBoardingInterviewStep.Portfolio,
                    showExistingPortfolioModal = true,
                ),
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

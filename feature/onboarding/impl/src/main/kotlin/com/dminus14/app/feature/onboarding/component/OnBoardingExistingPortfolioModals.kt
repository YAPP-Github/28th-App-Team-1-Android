package com.dminus14.app.feature.onboarding.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.dminus14.app.feature.onboarding.ExistingPortfolioModalPhase
import com.dminus14.app.feature.onboarding.OnBoardingInterviewIntent
import com.dminus14.app.feature.onboarding.OnBoardingInterviewState
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.modal.HilitBookIllustration
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalType
import kotlinx.coroutines.delay

internal const val AUTO_DISMISS_NOTICE_MS = 3_000L

@Composable
internal fun OnBoardingExistingPortfolioModals(
    state: OnBoardingInterviewState,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
) {
    when (state.existingPortfolioModalPhase) {
        ExistingPortfolioModalPhase.ConfirmContinue -> {
            OnBoardingExistingPortfolioConfirmModal(onIntent = onIntent)
        }

        ExistingPortfolioModalPhase.ConfirmLeaveToMyPage -> {
            OnBoardingLeaveToMyPageModal(
                deleteRemainingCount = state.deleteRemainingCount,
                onIntent = onIntent,
            )
        }

        ExistingPortfolioModalPhase.AutoDismissNotice -> {
            OnBoardingUseExistingPortfolioNotice(onIntent = onIntent)
        }

        ExistingPortfolioModalPhase.None -> {
            Unit
        }
    }
}

/** Case1 1차: 기존 포트폴리오 재사용 확인. */
@Composable
internal fun OnBoardingExistingPortfolioConfirmModal(
    onIntent: (OnBoardingInterviewIntent) -> Unit,
) {
    HilitModal(
        type = HilitModalType.InvisibleInfo,
        title = "기존에 있는 포트폴리오로\n다시 진행할 수 있어요",
        dismissible = false,
        graphic = { HilitBookIllustration() },
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "취소",
                rightText = "진행할게요",
                onLeftClick = { onIntent(OnBoardingInterviewIntent.ClickExistingPortfolioCancel) },
                onRightClick = { onIntent(OnBoardingInterviewIntent.ClickExistingPortfolioContinue) },
            )
        },
    )
}

/** Case1 2차: 마이페이지 이동 확인. */
@Composable
internal fun OnBoardingLeaveToMyPageModal(
    deleteRemainingCount: Int,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
) {
    HilitModal(
        type = HilitModalType.InvisibleIcon,
        title = "마이페이지로 이동하시겠어요?",
        subtitle = "기존에 업로드한 포트폴리오 파일을\n삭제하고, 다시 업로드할 수 있어요.",
        infoText = "이번 달 남은 삭제 기회 ${deleteRemainingCount}번",
        dismissible = false,
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "취소",
                rightText = "이동",
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = { onIntent(OnBoardingInterviewIntent.ClickLeaveToMyPageCancel) },
                onRightClick = { onIntent(OnBoardingInterviewIntent.ClickLeaveToMyPageConfirm) },
            )
        },
    )
}

/** Case2: 기존 포트폴리오 자동 사용 안내(3초 후 또는 배경 탭 시 dismiss). */
@Composable
internal fun OnBoardingUseExistingPortfolioNotice(
    onIntent: (OnBoardingInterviewIntent) -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(AUTO_DISMISS_NOTICE_MS)
        onIntent(OnBoardingInterviewIntent.AutoDismissNoticeTimedOut)
    }

    HilitModal(
        type = HilitModalType.InvisibleIcon,
        title = "가장 최근에 업로드한 포트폴리오를\n이번 면접에 그대로 사용합니다.",
        infoText = "이번 달 남은 삭제 기회 0번이라\n새로운 포트폴리오 업로드가 불가능해요.",
        dismissible = true,
        onDismiss = { onIntent(OnBoardingInterviewIntent.ClickAutoDismissNotice) },
        buttons = {},
    )
}

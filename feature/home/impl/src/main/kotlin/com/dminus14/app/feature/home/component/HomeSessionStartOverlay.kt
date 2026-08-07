package com.dminus14.app.feature.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 홈 화면에서 상태에 따라 페이드인으로 노출되는 세션 시작 오버레이 5종의 dispatcher.
 *
 * [state]가 null이면 아무것도 렌더하지 않고, non-null이면 해당 [HomeSessionStartOverlayState]
 * variant를 페이드인으로 표시한다.
 *
 * 실제 상태 트리거/버튼 콜백 배선은 후속 작업에서 [HomeSessionStartCallbacks]를 통해 주입.
 */
@Composable
internal fun HomeSessionStartOverlay(
    state: HomeSessionStartOverlayState?,
    callbacks: HomeSessionStartCallbacks,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = state != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize(),
    ) {
        when (val current = state) {
            is HomeSessionStartOverlayState.Start -> {
                HomeSessionStartStartVariant(state = current, callbacks = callbacks)
            }

            is HomeSessionStartOverlayState.ResumePortfolio -> {
                HomeSessionStartResumePortfolioVariant(state = current, callbacks = callbacks)
            }

            is HomeSessionStartOverlayState.NoTickets -> {
                HomeSessionStartNoTicketsVariant(state = current, callbacks = callbacks)
            }

            is HomeSessionStartOverlayState.InProgress -> {
                HomeSessionStartInProgressVariant(state = current, callbacks = callbacks)
            }

            HomeSessionStartOverlayState.ConfirmRestart -> {
                HomeSessionStartConfirmRestartVariant(callbacks = callbacks)
            }

            null -> {
                // AnimatedVisibility exit 애니메이션이 재생되는 동안 잠깐 걸릴 수 있음.
            }
        }
    }
}

/** 홈 오버레이 5종의 데이터 payload. */
sealed interface HomeSessionStartOverlayState {
    /** 이용권이 남아 있고 처음 시작하는 사용자. */
    data class Start(
        val userName: String,
        val remainingTicketCount: Int,
    ) : HomeSessionStartOverlayState

    /** 이전 포트폴리오로 이어서 시작할지 확인. */
    data class ResumePortfolio(
        val fileName: String,
        val uploadedAt: String,
        val sizeText: String,
    ) : HomeSessionStartOverlayState

    /** 무료 이용권을 모두 사용한 상태. */
    data class NoTickets(
        val userName: String,
    ) : HomeSessionStartOverlayState

    /** 진행 중인 면접이 있는 상태. */
    data class InProgress(
        val userName: String,
        val remainingQuestionCount: Int,
    ) : HomeSessionStartOverlayState

    /** "처음부터 시작" 재확인 다이얼로그 상태. */
    data object ConfirmRestart : HomeSessionStartOverlayState
}

/**
 * 오버레이 5종이 발생시킬 수 있는 모든 클릭 콜백.
 * 각 variant는 자신에게 필요한 콜백만 호출한다.
 */
data class HomeSessionStartCallbacks(
    val onCloseClick: () -> Unit = {},
    val onStartClick: () -> Unit = {},
    val onEditPortfolioClick: () -> Unit = {},
    val onGoHomeClick: () -> Unit = {},
    val onRestartClick: () -> Unit = {},
    val onResumeClick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
)

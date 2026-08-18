package com.dminus14.app.feature.home

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.dminus14.app.feature.home.component.screen.HomeSessionStartConfirmRestartVariant
import com.dminus14.app.feature.home.component.screen.HomeSessionStartInProgressVariant
import com.dminus14.app.feature.home.component.screen.HomeSessionStartNoTicketsVariant
import com.dminus14.app.feature.home.component.screen.HomeSessionStartStartVariant
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 홈에서 상태에 따라 페이드인으로 노출되는 세션 시작 오버레이 dispatcher.
 *
 * navigation destination은 아니고 `HomeScreen`의 상위 `Box`에 sibling으로 얹혀
 * [state]가 non-null일 때만 자기 자신을 그린다. 4개 variant는
 * `feature/home/impl/.../component/screen/`에 각각 stateless composable로 존재하고,
 * 이 컴포저블은 sealed [HomeSessionStartOverlayState] 값에 따라 그 중 하나를 라우팅한다.
 *
 * 상태 트리거·버튼 콜백은 [HomeSessionStartCallbacks]로 주입.
 */
@Composable
internal fun HomeSessionStartOverlay(
    state: HomeSessionStartOverlayState?,
    callbacks: HomeSessionStartCallbacks,
    modifier: Modifier = Modifier,
) {
    // 오버레이 4종 전부 하단 버튼(HilitFixedBottomButton/HilitFixedBottomDualButton)이 검정
    // 배경이다. Home 화면은 MainActivity의 Scaffold가 네비게이션 바 영역을 미리 예약해 콘텐츠가
    // 그 뒤까지 그려지지 않으므로(#166 InterviewReport 처럼 화면 전체를 FullScreen 으로 바꾸는
    // 대신), 레이아웃은 그대로 두고 시스템 네비게이션 바 색상 자체를 버튼과 맞춰 인디케이터
    // 영역까지 색이 이어지는 것처럼 보이게 한다(#179). 오버레이가 닫히면 원래 색으로 되돌린다.
    MatchNavigationBarToOverlay(visible = state != null)

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
                // AnimatedVisibility exit 애니메이션 재생 도중 잠깐 걸릴 수 있음.
            }
        }
    }
}

/**
 * 오버레이가 보이는 동안 시스템 네비게이션 바를 오버레이 하단 버튼과 같은 검정으로 칠하고,
 * 오버레이가 닫히거나 이 컴포저블이 화면에서 사라지면(예: 다른 화면으로 이동) 원래 상태
 * (투명·밝은 아이콘)로 되돌린다.
 */
@Composable
private fun MatchNavigationBarToOverlay(visible: Boolean) {
    val activity = LocalActivity.current
    val overlayNavigationBarColor = HilitTheme.colors.hilitBlack800
    DisposableEffect(visible) {
        val window = activity?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            if (visible) {
                @Suppress("DEPRECATION")
                window.navigationBarColor = overlayNavigationBarColor.toArgb()
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
        onDispose {
            if (window != null) {
                @Suppress("DEPRECATION")
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat
                    .getInsetsController(window, window.decorView)
                    .isAppearanceLightNavigationBars = true
            }
        }
    }
}

/** 홈 오버레이 4종의 데이터 payload. */
sealed interface HomeSessionStartOverlayState {
    /** 이용권이 남아 있고 처음 시작하는 사용자. */
    data class Start(
        val userName: String,
        val remainingTicketCount: Int,
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
 * 오버레이 4종이 발생시킬 수 있는 모든 클릭 콜백.
 * 각 variant는 자신에게 필요한 콜백만 호출한다.
 */
data class HomeSessionStartCallbacks(
    val onCloseClick: () -> Unit = {},
    val onStartClick: () -> Unit = {},
    val onGoHomeClick: () -> Unit = {},
    val onRestartClick: () -> Unit = {},
    val onResumeClick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
)

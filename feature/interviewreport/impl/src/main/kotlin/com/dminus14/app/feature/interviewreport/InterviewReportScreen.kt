@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 1차 리포트 화면.
 *
 * ViewModel Effect 를 상위 Navigation 콜백으로 승격한다 (navigation.md §2 계약).
 */
@Composable
@Suppress("LongParameterList")
fun InterviewReportScreen(
    sessionId: Long,
    onNavigateBack: () -> Unit,
    onWatchVideo: (sessionId: Long, startSec: Float?) -> Unit,
    onOpenGuestFeedback: (sessionId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterviewReportViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // FullScreen(상태바·네비게이션바 뒤까지 hilitBlack900 배경이 깔림)이라 아이콘이 항상 밝게
    // (흰색) 보이도록 고정한다. MyPageScreen 의 MatchSystemBarsToContent 와 같은 목적이되,
    // 여기는 다크 배경 고정이라 라이트 모드에서도 흰 아이콘을 강제한다.
    MatchSystemBarsToDarkContent()

    LaunchedEffect(sessionId) {
        viewModel.bindSessionId(sessionId)
        viewModel.onIntent(InterviewReportIntent.Load)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                InterviewReportEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is InterviewReportEffect.NavigateToPlayer -> {
                    onWatchVideo(effect.sessionId, effect.startSec)
                }

                is InterviewReportEffect.NavigateToGuestFeedback -> {
                    onOpenGuestFeedback(effect.sessionId)
                }

                is InterviewReportEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    InterviewReportContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * 상태바·네비게이션바 아이콘을 항상 밝게(흰색) 고정한다. `enableEdgeToEdge()` 는 시스템
 * 다크/라이트 모드 기준으로만 아이콘 밝기를 정해, 라이트 모드 기기에서 이 화면의 다크 배경과
 * 어긋날 수 있다.
 */
@Composable
private fun MatchSystemBarsToDarkContent() {
    val activity = LocalActivity.current
    SideEffect {
        activity?.window?.let { window ->
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }
}

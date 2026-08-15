@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat

/**
 * 상태바·네비게이션바 아이콘을 항상 밝게(흰색) 고정한다. `enableEdgeToEdge()`는 시스템
 * 다크/라이트 모드 기준으로만 아이콘 밝기를 정해, 라이트 모드 기기에서 이 Feature 의 다크
 * 배경(리포트 화면 hilitBlack900, 플레이어 화면 FullScreen 영상)과 어긋날 수 있다.
 *
 * `InterviewReportScreen`과 `InterviewReportPlayerScreen`이 함께 쓴다.
 */
@Composable
internal fun MatchSystemBarsToDarkContent() {
    val activity = LocalActivity.current
    SideEffect {
        activity?.window?.let { window ->
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }
}

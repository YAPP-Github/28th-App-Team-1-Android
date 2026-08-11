package com.dminus14.app.feature.interviewreport.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 영상 플레이어 화면 skeleton.
 *
 * 실제 Media3 ExoPlayer 배선과 STT 오버레이는 후속 티켓에서 다룬다 (결정 필요 #7).
 * 이번 스코프에서는 만료 안내와 되돌아가기 진입점만 제공한다.
 */
@Composable
fun InterviewReportPlayerScreen(
    sessionId: Long,
    startSec: Float?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.hilitBlack900)
                .clickable(onClick = onNavigateBack),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "영상 재생은 준비 중이에요.",
                style = HilitTheme.typography.sub4,
                color = colors.hilitWhite,
            )
            Text(
                text = "sessionId=$sessionId · startSec=${startSec ?: 0f}",
                style = HilitTheme.typography.body7,
                color = colors.gray300,
            )
            Text(
                text = "화면을 눌러 리포트로 돌아가요.",
                style = HilitTheme.typography.body7,
                color = colors.gray400,
            )
        }
    }
}

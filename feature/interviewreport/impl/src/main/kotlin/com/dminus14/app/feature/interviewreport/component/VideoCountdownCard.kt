package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.VideoUiModel
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay

private const val SECONDS_PER_HOUR = 3600L
private const val SECONDS_PER_MINUTE = 60L

/**
 * 면접 영상 다시보기 진입 카드 + 남은시간 카운트다운 (Figma Node: 443:6897).
 *
 * - [video] 가 null 이면 렌더하지 않는다.
 * - [expirySeconds] 시드로 1초 간격 카운트다운을 렌더한다. 만료(`video.expired`)면 `00:00:00`,
 *   조회 실패로 [expirySeconds] 가 null 이면(그러나 만료는 아님) 카운트다운 행을 숨긴다.
 */
@Composable
internal fun VideoCountdownCard(
    video: VideoUiModel?,
    expirySeconds: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (video == null) return
    val colors = HilitTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.hilitBlack800)
                    .clickable(onClick = onClick)
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "면접 영상 다시보기",
                    style = HilitTheme.typography.sub7,
                    color = colors.gray50,
                )
                HilitIcon(
                    asset = HilitIconAsset.Right,
                    contentDescription = null,
                    tint = colors.gray400,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (video.expired) {
                CountdownRow(
                    remainingText = formatCountdown(0L),
                    note = video.expiredNotice ?: "영상이 만료되어 다시 볼 수 없어요.",
                )
            } else if (expirySeconds != null) {
                var remaining by remember(expirySeconds) { mutableLongStateOf(expirySeconds) }
                LaunchedEffect(expirySeconds) {
                    remaining = expirySeconds
                    while (remaining > 0L) {
                        delay(1000L)
                        remaining -= 1L
                    }
                }
                CountdownRow(
                    remainingText = formatCountdown(remaining),
                    note = "이 시간이 지나면\n면접 영상을 볼 수 없어요.",
                )
            }
        }
        Text(
            text = "* 지인 1명이라도 피드백이 완료될 시 시청 시간이 최대로 연장됩니다.",
            style = HilitTheme.typography.body9,
            color = colors.gray600,
        )
    }
}

@Composable
private fun CountdownRow(
    remainingText: String,
    note: String,
) {
    val colors = HilitTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.gray800),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Timer,
                    contentDescription = null,
                    tint = colors.hilitGreen500,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = note,
                    style = HilitTheme.typography.body9,
                    // TODO(designsystem): Figma #D2D6DE(레거시 Gray scale/300) 대응 토큰 확정 필요, 임시 gray200.
                    color = colors.gray200,
                )
            }
            Text(
                text = remainingText,
                style = HilitTheme.typography.sub3,
                color = colors.hilitWhite,
                textAlign = TextAlign.End,
            )
        }
    }
}

private fun formatCountdown(totalSeconds: Long): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val hours = safe / SECONDS_PER_HOUR
    val minutes = (safe % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val seconds = safe % SECONDS_PER_MINUTE
    return buildString {
        append(hours.toString().padStart(2, '0'))
        append(':')
        append(minutes.toString().padStart(2, '0'))
        append(':')
        append(seconds.toString().padStart(2, '0'))
    }
}

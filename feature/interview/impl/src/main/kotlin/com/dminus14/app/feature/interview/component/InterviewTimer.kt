package com.dminus14.app.feature.interview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import java.util.Locale

private const val MINUTE_SECONDS = 60

/**
 * 면접 타이머.
 *
 * Figma 시안:
 * - 60초 초과: 683:9276
 * - 60초 이하: 683:9308
 */
@Composable
fun InterviewTimer(
    remainingSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val isWarning = (remainingSeconds <= MINUTE_SECONDS)
    val bgColor = if (isWarning) HilitTheme.colors.error200 else HilitTheme.colors.gray50
    val textColor = if (isWarning) HilitTheme.colors.error500 else HilitTheme.colors.gray500

    // 60초 이상일 땐 MM:SS, 이하일 땐 SS초
    val minutes = remainingSeconds / MINUTE_SECONDS
    val seconds = remainingSeconds % MINUTE_SECONDS
    val timeText =
        if (remainingSeconds < MINUTE_SECONDS) {
            "${remainingSeconds.coerceIn(0, MINUTE_SECONDS)}초"
        } else {
            String.format(Locale.US, "%d:%02d", minutes, seconds)
        }

    Box(
        modifier =
            modifier
                .size(width = 65.dp, height = 21.dp)
                .background(color = bgColor, shape = TrapezoidShape()),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            HilitIcon(
                asset = HilitIconAsset.Timer,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = timeText,
                style = HilitTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = textColor,
            )
        }
    }
}

@Preview
@Composable
private fun InterviewTimerPreview() {
    HilitTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            InterviewTimer(remainingSeconds = 80) // 1:20
            InterviewTimer(remainingSeconds = 10) // 0:10 (warning style)
        }
    }
}

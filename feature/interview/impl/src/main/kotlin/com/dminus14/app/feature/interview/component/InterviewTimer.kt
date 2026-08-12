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
private const val COUNTDOWN_SECONDS = 10

/**
 * 면접 타이머.
 *
 * Figma 시안:
 * - 60초 초과: 683:9276
 * - 60초 이하: 683:9308
 */
@Composable
fun InterviewTimer(
    elapsedSeconds: Int,
    modifier: Modifier = Modifier,
    countdownSeconds: Int? = null,
) {
    val isWarning = countdownSeconds != null
    val bgColor = if (isWarning) HilitTheme.colors.error200 else HilitTheme.colors.gray50
    val textColor = if (isWarning) HilitTheme.colors.error500 else HilitTheme.colors.gray500

    val minutes = elapsedSeconds / MINUTE_SECONDS
    val seconds = elapsedSeconds % MINUTE_SECONDS
    val timeText =
        if (countdownSeconds != null) {
            "${countdownSeconds.coerceIn(0, COUNTDOWN_SECONDS)}초"
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
            InterviewTimer(elapsedSeconds = 80)
            InterviewTimer(elapsedSeconds = 710, countdownSeconds = 10)
        }
    }
}

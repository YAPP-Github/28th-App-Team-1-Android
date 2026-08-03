package com.dminus14.app.feature.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private const val VIDEO_OVERLAY_ALPHA = 0.65f
private val SEEK_BUTTON_SIZE = 44.dp
private val PLAYBACK_BUTTON_SIZE = 74.dp
private val VIDEO_CONTROL_ICON_SIZE = 34.dp
private val VIDEO_CONTROL_SPACING = 46.dp

internal data class VideoControlActions(
    val onDismiss: () -> Unit,
    val onSeekBackward: () -> Unit,
    val onTogglePlayback: () -> Unit,
    val onSeekForward: () -> Unit,
)

@Composable
internal fun GuestFeedbackVideoControls(
    isPlaying: Boolean,
    actions: VideoControlActions,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitBlack800.copy(alpha = VIDEO_OVERLAY_ALPHA))
                .clickable(onClick = actions.onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VIDEO_CONTROL_SPACING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SeekVideoControlButton(
                asset = HilitIconAsset.SkipLeft,
                contentDescription = "10초 전으로 이동",
                onClick = actions.onSeekBackward,
            )
            PlaybackVideoControlButton(
                isPlaying = isPlaying,
                onClick = actions.onTogglePlayback,
            )
            SeekVideoControlButton(
                asset = HilitIconAsset.SkipRight,
                contentDescription = "10초 후로 이동",
                onClick = actions.onSeekForward,
            )
        }
    }
}

@Composable
private fun SeekVideoControlButton(
    asset: HilitIconAsset,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(SEEK_BUTTON_SIZE)
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        HilitIcon(
            asset = asset,
            contentDescription = contentDescription,
            tint = HilitTheme.colors.hilitWhite,
            modifier = Modifier.size(VIDEO_CONTROL_ICON_SIZE),
        )
    }
}

@Composable
private fun PlaybackVideoControlButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(PLAYBACK_BUTTON_SIZE)
                .background(HilitTheme.colors.hilitGreen500)
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        HilitIcon(
            asset = if (isPlaying) HilitIconAsset.Pause else HilitIconAsset.Play,
            contentDescription = if (isPlaying) "일시정지" else "재생",
            tint = HilitTheme.colors.hilitGreen800,
            modifier = Modifier.size(VIDEO_CONTROL_ICON_SIZE),
        )
    }
}

@Preview(
    name = "재생 컨트롤 표시",
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun GuestFeedbackVideoPlayerWithControlsPreview() {
    GuestFeedbackVideoPlayerPreviewContent(showControls = true)
}

@Preview(
    name = "최초 안내 오버레이 표시",
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun GuestFeedbackVideoPlayerIntroPreview() {
    GuestFeedbackVideoPlayerPreviewContent(showIntro = true)
}

@Composable
private fun GuestFeedbackVideoPlayerPreviewContent(
    showControls: Boolean = false,
    showIntro: Boolean = false,
) {
    HilitTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(HilitTheme.colors.gray700),
        ) {
            if (showControls) {
                GuestFeedbackVideoControls(
                    isPlaying = true,
                    actions =
                        VideoControlActions(
                            onDismiss = {},
                            onSeekBackward = {},
                            onTogglePlayback = {},
                            onSeekForward = {},
                        ),
                )
            }
            if (showIntro) {
                GuestFeedbackVideoIntroOverlay(
                    requesterName = "홍길동",
                    introAlpha = 1f,
                    onDismiss = {},
                )
            }
        }
    }
}

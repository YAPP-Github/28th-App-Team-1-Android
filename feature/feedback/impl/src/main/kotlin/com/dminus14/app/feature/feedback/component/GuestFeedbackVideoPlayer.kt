package com.dminus14.app.feature.feedback.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay

/** 영상 실행 상태와 Player 수명을 한 곳에서 소유하는 Feedback 전용 Composable이다. */
@Composable
@androidx.annotation.OptIn(UnstableApi::class)
@Suppress("LongMethod", "LongParameterList")
fun GuestFeedbackVideoPlayer(
    videoUrl: String,
    isIntroVisible: Boolean,
    requesterName: String,
    showBlurredBackdrop: Boolean,
    onIntroCompleted: () -> Unit,
    onExpand: () -> Unit,
    onFatalPlaybackError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val player =
        remember(videoUrl) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
            }
        }
    var isPlaying by remember(player) { mutableStateOf(false) }
    var didReportError by remember(player) { mutableStateOf(false) }
    var didRecoverVideoEffect by remember(player) { mutableStateOf(false) }
    var introDismissRequested by remember(isIntroVisible) { mutableStateOf(false) }
    val introAlpha by
        animateFloatAsState(
            targetValue = if (introDismissRequested) 0f else 1f,
            animationSpec = tween(INTRO_FADE_MILLIS),
            label = "guest-feedback-video-intro",
        )

    DisposableEffect(player, lifecycleOwner, showBlurredBackdrop) {
        val playerListener =
            object : Player.Listener {
                override fun onIsPlayingChanged(value: Boolean) {
                    isPlaying = value
                }

                override fun onPlayerError(error: PlaybackException) {
                    if (shouldRecoverVideoEffect(
                            showBlurredBackdrop,
                            didRecoverVideoEffect,
                            error.errorCode,
                        )
                    ) {
                        didRecoverVideoEffect = true
                        val position = player.currentPosition
                        val shouldResume = player.playWhenReady
                        player.setVideoEffects(emptyList())
                        player.prepare()
                        player.seekTo(position)
                        player.playWhenReady = shouldResume
                        return
                    }
                    if (!didReportError) {
                        didReportError = true
                        onFatalPlaybackError()
                    }
                }
            }
        val lifecycleObserver =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) player.pause()
            }
        player.addListener(playerListener)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            player.removeListener(playerListener)
            player.release()
        }
    }

    LaunchedEffect(player, showBlurredBackdrop, didRecoverVideoEffect) {
        player.setVideoEffects(
            if (showBlurredBackdrop && !didRecoverVideoEffect) {
                listOf(GuestFeedbackVideoPresentationEffect())
            } else {
                emptyList()
            },
        )
    }

    LaunchedEffect(isIntroVisible, introDismissRequested) {
        if (!isIntroVisible) {
            player.play()
            return@LaunchedEffect
        }
        if (!introDismissRequested) {
            delay(INTRO_VISIBLE_MILLIS)
            introDismissRequested = true
            return@LaunchedEffect
        }
        delay(INTRO_FADE_MILLIS.toLong())
        player.play()
        onIntroCompleted()
    }

    Box(
        modifier =
            modifier
                .background(Color.Black)
                .semantics {
                    contentDescription = if (isPlaying) "재생 중인 면접 영상" else "일시정지된 면접 영상"
                }.clickable(enabled = !isIntroVisible) {
                    if (player.isPlaying) player.pause() else player.play()
                },
    ) {
        PlayerSurface(
            player = player,
            modifier = Modifier.fillMaxSize(),
        )

        if (showBlurredBackdrop) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = VIDEO_CONTROLS_BOTTOM_PADDING)
                        .size(30.dp)
                        .background(HilitTheme.colors.gray700)
                        .clickable(
                            role = Role.Button,
                            onClick = onExpand,
                        ).semantics {
                            role = Role.Button
                            contentDescription = "영상 확대"
                        },
                contentAlignment = Alignment.Center,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Expand,
                    contentDescription = null,
                    tint = HilitTheme.colors.hilitWhite,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        if (isIntroVisible) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .alpha(introAlpha)
                        .background(Color.Black.copy(alpha = 0.72f))
                        .clickable { introDismissRequested = true },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$requesterName 님의 태도도 함께 살펴봐 주세요",
                    color = HilitTheme.colors.hilitWhite,
                    style = HilitTheme.typography.head5,
                )
            }
        }
    }
}

private const val INTRO_VISIBLE_MILLIS = 2_000L
private const val INTRO_FADE_MILLIS = 300
private val VIDEO_CONTROLS_BOTTOM_PADDING = 76.dp

@androidx.annotation.OptIn(UnstableApi::class)
internal fun shouldRecoverVideoEffect(
    showBlurredBackdrop: Boolean,
    didRecoverVideoEffect: Boolean,
    errorCode: Int,
): Boolean =
    showBlurredBackdrop &&
        !didRecoverVideoEffect &&
        errorCode == PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSING_FAILED

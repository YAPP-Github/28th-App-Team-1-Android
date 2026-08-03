package com.dminus14.app.feature.feedback.component

import androidx.annotation.OptIn
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/** 영상 실행 상태와 Player 수명을 한 곳에서 소유하는 Feedback 전용 Composable이다. */
@Composable
@OptIn(UnstableApi::class)
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
    var videoSize by remember(videoUrl) { mutableStateOf<VideoSize?>(null) }
    var outputSize by remember { mutableStateOf(IntSize.Zero) }

    val inputWidth = videoSize?.width ?: 0
    val inputHeight = videoSize?.height ?: 0
    val sharpFrameScale =
        calculateSharpFrameScale(
            inputWidth = inputWidth,
            inputHeight = inputHeight,
            outputWidth = outputSize.width,
            outputHeight = outputSize.height,
        )
    val videoEffect =
        remember(sharpFrameScale, inputWidth, inputHeight) {
            GuestFeedbackVideoPresentationEffect(
                inputWidth = inputWidth,
                inputHeight = inputHeight,
                sharpFrameScaleX = sharpFrameScale.x,
                sharpFrameScaleY = sharpFrameScale.y,
            )
        }
    val player =
        remember(videoUrl) {
            ExoPlayer.Builder(context).build().also { player ->
                configureAndPrepareGuestFeedbackPlayer(
                    player = player,
                    mediaItem = MediaItem.fromUri(videoUrl),
                    showBlurredBackdrop = showBlurredBackdrop,
                    videoEffect = videoEffect,
                )
            }
        }
    var isPlaying by remember(player) { mutableStateOf(false) }
    var arePlaybackControlsVisible by remember(player) { mutableStateOf(false) }
    var introDismissRequested by remember(isIntroVisible) { mutableStateOf(false) }
    val introAlpha by
        animateFloatAsState(
            targetValue = if (introDismissRequested) 0f else 1f,
            animationSpec = tween(INTRO_FADE_MILLIS),
            label = "guest-feedback-video-intro",
        )

    GuestFeedbackPlayerLifecycle(
        player = player,
        lifecycleOwner = lifecycleOwner,
        showBlurredBackdrop = showBlurredBackdrop,
        videoEffect = videoEffect,
        onIsPlayingChanged = { isPlaying = it },
        onVideoSizeChanged = { videoSize = it },
        onFatalPlaybackError = onFatalPlaybackError,
    )

    GuestFeedbackIntroController(
        player = player,
        isIntroVisible = isIntroVisible,
        introDismissRequested = introDismissRequested,
        onDismissRequested = { introDismissRequested = true },
        onIntroCompleted = onIntroCompleted,
    )

    val controlActions =
        rememberVideoControlActions(
            player = player,
            onDismiss = { arePlaybackControlsVisible = false },
        )

    Box(
        modifier =
            modifier
                .onSizeChanged { outputSize = it }
                .background(Color.Black)
                .semantics {
                    contentDescription = if (isPlaying) "재생 중인 면접 영상" else "일시정지된 면접 영상"
                }.clickable(enabled = !isIntroVisible) {
                    arePlaybackControlsVisible = !arePlaybackControlsVisible
                },
    ) {
        PlayerSurface(
            player = player,
            modifier = Modifier.fillMaxSize(),
        )

        if (showBlurredBackdrop) {
            GuestFeedbackVideoExpandButton(
                onExpand = onExpand,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = VIDEO_CONTROLS_BOTTOM_PADDING),
            )
        }

        if (arePlaybackControlsVisible && !isIntroVisible) {
            GuestFeedbackVideoControls(
                isPlaying = isPlaying,
                actions = controlActions,
            )
        }

        if (isIntroVisible) {
            GuestFeedbackVideoIntroOverlay(
                requesterName = requesterName,
                introAlpha = introAlpha,
                onDismiss = { introDismissRequested = true },
            )
        }
    }
}

@Composable
private fun GuestFeedbackIntroController(
    player: ExoPlayer,
    isIntroVisible: Boolean,
    introDismissRequested: Boolean,
    onDismissRequested: () -> Unit,
    onIntroCompleted: () -> Unit,
) {
    LaunchedEffect(isIntroVisible, introDismissRequested) {
        if (!isIntroVisible) {
            player.play()
            return@LaunchedEffect
        }
        if (!introDismissRequested) {
            delay(INTRO_VISIBLE_MILLIS.milliseconds)
            onDismissRequested()
            return@LaunchedEffect
        }
        delay(INTRO_FADE_MILLIS.milliseconds)
        player.play()
        onIntroCompleted()
    }
}

@Composable
@OptIn(UnstableApi::class)
private fun rememberVideoControlActions(
    player: ExoPlayer,
    onDismiss: () -> Unit,
): VideoControlActions =
    remember(player, onDismiss) {
        VideoControlActions(
            onDismiss = onDismiss,
            onSeekBackward = {
                player.seekTo(
                    seekPositionAfterOffset(
                        currentPositionMillis = player.currentPosition,
                        durationMillis = player.duration,
                        offsetMillis = -VIDEO_SEEK_OFFSET_MILLIS,
                    ),
                )
            },
            onTogglePlayback = {
                if (player.isPlaying) player.pause() else player.play()
            },
            onSeekForward = {
                player.seekTo(
                    seekPositionAfterOffset(
                        currentPositionMillis = player.currentPosition,
                        durationMillis = player.duration,
                        offsetMillis = VIDEO_SEEK_OFFSET_MILLIS,
                    ),
                )
            },
        )
    }

@Composable
private fun GuestFeedbackVideoExpandButton(
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
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

@Composable
internal fun GuestFeedbackVideoIntroOverlay(
    requesterName: String,
    introAlpha: Float,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .alpha(introAlpha)
                .background(Color.Black.copy(alpha = 0.72f))
                .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$requesterName 님의 태도도 함께 살펴봐 주세요",
            color = HilitTheme.colors.hilitWhite,
            style = HilitTheme.typography.head5,
        )
    }
}

@OptIn(UnstableApi::class)
internal fun configureAndPrepareGuestFeedbackPlayer(
    player: ExoPlayer,
    mediaItem: MediaItem,
    showBlurredBackdrop: Boolean,
    videoEffect: GuestFeedbackVideoPresentationEffect,
) {
    player.setMediaItem(mediaItem)
    player.setVideoEffects(if (showBlurredBackdrop) listOf(videoEffect) else emptyList())
    player.prepare()
}

/** Player listener와 Lifecycle observer의 등록·해제를 Player 인스턴스 수명에 연결한다. */
@Composable
@OptIn(UnstableApi::class)
@Suppress("LongParameterList")
internal fun GuestFeedbackPlayerLifecycle(
    player: ExoPlayer,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    showBlurredBackdrop: Boolean,
    videoEffect: GuestFeedbackVideoPresentationEffect,
    onIsPlayingChanged: (Boolean) -> Unit,
    onVideoSizeChanged: (VideoSize) -> Unit,
    onFatalPlaybackError: () -> Unit,
) {
    var didReportError by remember(player) { mutableStateOf(false) }
    var didRecoverVideoEffect by remember(player) { mutableStateOf(false) }
    val currentShowBlurredBackdrop by rememberUpdatedState(showBlurredBackdrop)
    val currentOnIsPlayingChanged by rememberUpdatedState(onIsPlayingChanged)
    val currentOnVideoSizeChanged by rememberUpdatedState(onVideoSizeChanged)
    val currentOnFatalPlaybackError by rememberUpdatedState(onFatalPlaybackError)

    DisposableEffect(player) {
        val playerListener =
            object : Player.Listener {
                override fun onIsPlayingChanged(value: Boolean) {
                    currentOnIsPlayingChanged(value)
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    currentOnVideoSizeChanged(videoSize)
                }

                override fun onPlayerError(error: PlaybackException) {
                    if (shouldRecoverVideoEffect(
                            currentShowBlurredBackdrop,
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
                        currentOnFatalPlaybackError()
                    }
                }
            }
        player.addListener(playerListener)

        onDispose {
            player.removeListener(playerListener)
            player.release()
        }
    }

    DisposableEffect(lifecycleOwner, player) {
        val lifecycleObserver =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) player.pause()
            }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(player, showBlurredBackdrop, didRecoverVideoEffect, videoEffect) {
        player.setVideoEffects(
            if (showBlurredBackdrop && !didRecoverVideoEffect) {
                listOf(videoEffect)
            } else {
                emptyList()
            },
        )
    }
}

internal fun seekPositionAfterOffset(
    currentPositionMillis: Long,
    durationMillis: Long,
    offsetMillis: Long,
): Long {
    val maximumPosition =
        durationMillis.takeIf { it != C.TIME_UNSET && it >= 0L } ?: Long.MAX_VALUE
    return (currentPositionMillis + offsetMillis).coerceIn(0L, maximumPosition)
}

private const val INTRO_VISIBLE_MILLIS = 2_000L
private const val INTRO_FADE_MILLIS = 300
private const val VIDEO_SEEK_OFFSET_MILLIS = 10_000L
private val VIDEO_CONTROLS_BOTTOM_PADDING = 76.dp

@OptIn(UnstableApi::class)
internal fun shouldRecoverVideoEffect(
    showBlurredBackdrop: Boolean,
    didRecoverVideoEffect: Boolean,
    errorCode: Int,
): Boolean =
    showBlurredBackdrop &&
        !didRecoverVideoEffect &&
        errorCode == PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSING_FAILED

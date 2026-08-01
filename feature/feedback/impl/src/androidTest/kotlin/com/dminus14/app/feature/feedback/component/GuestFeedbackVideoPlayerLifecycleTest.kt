package com.dminus14.app.feature.feedback.component

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GuestFeedbackVideoPlayerLifecycleTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `블러 표시를 전환해도 플레이어를 해제하지 않고 화면 이탈 때 한 번 해제한다`() {
        val player = mockk<ExoPlayer>(relaxed = true)
        val lifecycleOwner = TestLifecycleOwner()
        var showBlurredBackdrop by mutableStateOf(false)
        var isVisible by mutableStateOf(true)

        composeRule.setContent {
            if (isVisible) {
                TestPlayerLifecycle(
                    player = player,
                    lifecycleOwner = lifecycleOwner,
                    showBlurredBackdrop = showBlurredBackdrop,
                )
            }
        }

        composeRule.runOnIdle { showBlurredBackdrop = true }
        composeRule.runOnIdle { showBlurredBackdrop = false }
        verify(exactly = 0) { player.release() }

        composeRule.runOnIdle { isVisible = false }

        verify(exactly = 1) { player.release() }
    }

    @Test
    fun `수명 주기가 중지되면 플레이어를 일시정지한다`() {
        val player = mockk<ExoPlayer>(relaxed = true)
        val lifecycleOwner = TestLifecycleOwner()
        composeRule.setContent {
            TestPlayerLifecycle(player = player, lifecycleOwner = lifecycleOwner)
        }

        composeRule.runOnIdle {
            lifecycleOwner.registry.handleLifecycleEvent(
                Lifecycle.Event.ON_STOP,
            )
        }

        verify(exactly = 1) { player.pause() }
    }

    @Test
    fun `효과 실패는 재생 위치를 보존해 한 번 복구하고 다음 실패만 치명 오류로 전달한다`() {
        val player = mockk<ExoPlayer>(relaxed = true)
        val listener = slot<Player.Listener>()
        val lifecycleOwner = TestLifecycleOwner()
        var fatalErrorCount = 0
        every { player.addListener(capture(listener)) } just Runs
        every { player.currentPosition } returns 1_234L
        every { player.playWhenReady } returns true
        every { player.playWhenReady = any() } just Runs
        composeRule.setContent {
            TestPlayerLifecycle(
                player = player,
                lifecycleOwner = lifecycleOwner,
                showBlurredBackdrop = true,
                onFatalPlaybackError = { fatalErrorCount++ },
            )
        }
        composeRule.waitForIdle()
        val error =
            PlaybackException(
                "synthetic effect failure",
                null,
                PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSING_FAILED,
            )

        composeRule.runOnIdle { listener.captured.onPlayerError(error) }

        verify(exactly = 1) { player.prepare() }
        verify(exactly = 1) { player.seekTo(1_234L) }
        verify(exactly = 1) { player.playWhenReady = true }
        assertEquals(0, fatalErrorCount)

        composeRule.runOnIdle { listener.captured.onPlayerError(error) }

        assertEquals(1, fatalErrorCount)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    @androidx.compose.runtime.Composable
    private fun TestPlayerLifecycle(
        player: ExoPlayer,
        lifecycleOwner: LifecycleOwner,
        showBlurredBackdrop: Boolean = false,
        onFatalPlaybackError: () -> Unit = {},
    ) {
        GuestFeedbackPlayerLifecycle(
            player = player,
            lifecycleOwner = lifecycleOwner,
            showBlurredBackdrop = showBlurredBackdrop,
            videoEffect = GuestFeedbackVideoPresentationEffect(1f, 1f),
            onIsPlayingChanged = {},
            onVideoSizeChanged = {},
            onFatalPlaybackError = onFatalPlaybackError,
        )
    }

    private class TestLifecycleOwner : LifecycleOwner {
        val registry = LifecycleRegistry(this)

        override val lifecycle: Lifecycle = registry

        init {
            registry.currentState = Lifecycle.State.RESUMED
        }
    }
}

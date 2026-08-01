package com.dminus14.app.feature.feedback.component

import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuestFeedbackVideoPlayerTest {
    @Test
    fun `블러 효과의 첫 프레임 처리 실패만 원본 영상으로 복구한다`() {
        assertTrue(
            shouldRecoverVideoEffect(
                showBlurredBackdrop = true,
                didRecoverVideoEffect = false,
                errorCode = PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSING_FAILED,
            ),
        )
    }

    @Test
    fun `효과 복구 뒤 오류와 원본 재생 오류는 효과 실패로 복구하지 않는다`() {
        assertFalse(
            shouldRecoverVideoEffect(
                showBlurredBackdrop = true,
                didRecoverVideoEffect = true,
                errorCode = PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSING_FAILED,
            ),
        )
        assertFalse(
            shouldRecoverVideoEffect(
                showBlurredBackdrop = false,
                didRecoverVideoEffect = false,
                errorCode = PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            ),
        )
    }

    @Test
    fun `영상 탐색 위치는 앞뒤 십 초를 이동하고 재생 범위를 벗어나지 않는다`() {
        assertEquals(5_000L, seekPositionAfterOffset(15_000L, 30_000L, -10_000L))
        assertEquals(25_000L, seekPositionAfterOffset(15_000L, 30_000L, 10_000L))
        assertEquals(0L, seekPositionAfterOffset(5_000L, 30_000L, -10_000L))
        assertEquals(30_000L, seekPositionAfterOffset(25_000L, 30_000L, 10_000L))
    }

    @Test
    fun `영상 길이를 모를 때도 탐색 위치는 음수가 되지 않는다`() {
        assertEquals(10_000L, seekPositionAfterOffset(0L, C.TIME_UNSET, 10_000L))
        assertEquals(0L, seekPositionAfterOffset(0L, C.TIME_UNSET, -10_000L))
    }
}

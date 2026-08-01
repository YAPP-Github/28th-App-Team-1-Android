package com.dminus14.app.feature.feedback.component

import androidx.media3.common.PlaybackException
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
}

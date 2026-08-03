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

    @Test
    fun `세로 영상은 가로 출력 영역에서 종횡비를 유지한 채 너비에 맞춘다`() {
        val scale =
            calculateSharpFrameScale(
                inputWidth = 1_080,
                inputHeight = 1_920,
                outputWidth = 1_920,
                outputHeight = 1_080,
            )

        assertEquals(0.31640625f, scale.x, 0.0001f)
        assertEquals(1f, scale.y, 0f)
    }

    @Test
    fun `가로 영상은 세로 출력 영역에서 종횡비를 유지한 채 높이에 맞춘다`() {
        val scale =
            calculateSharpFrameScale(
                inputWidth = 1_920,
                inputHeight = 1_080,
                outputWidth = 1_080,
                outputHeight = 1_920,
            )

        assertEquals(1f, scale.x, 0f)
        assertEquals(0.31640625f, scale.y, 0.0001f)
    }

    @Test
    fun `영상이나 출력 크기를 모르면 원본 배율을 사용한다`() {
        assertEquals(
            SharpFrameScale(1f, 1f),
            calculateSharpFrameScale(0, 0, 1_080, 1_920),
        )
    }

    @Test
    fun `저해상도 프레임은 짧은 변에 비례해 흐림 반경을 줄인다`() {
        assertEquals(20f / 3f, calculateBlurSigmaPixels(360, 640), 0.0001f)
        assertEquals(20f, calculateBlurSigmaPixels(1_080, 1_920), 0f)
    }

    @Test
    fun `프레임 크기를 모르거나 기준보다 커도 안전한 흐림 범위를 사용한다`() {
        assertEquals(1f, calculateBlurSigmaPixels(0, 0), 0f)
        assertEquals(20f, calculateBlurSigmaPixels(2_160, 3_840), 0f)
    }
}

package com.dminus14.app.feature.feedback.component

import android.content.Context
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.GaussianBlurWithFrameOverlaid
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

/** 같은 영상 프레임으로 흐린 배경과 중앙의 선명한 전경을 합성하는 Feedback 전용 효과다. */
@UnstableApi
internal class GuestFeedbackVideoPresentationEffect : GlEffect {
    private val delegate =
        GaussianBlurWithFrameOverlaid(
            BLUR_SIGMA_PIXELS,
            SHARP_FRAME_SCALE_X,
            SHARP_FRAME_SCALE_Y,
        )

    @Throws(VideoFrameProcessingException::class)
    override fun toGlShaderProgram(
        context: Context,
        useHdr: Boolean,
    ): GlShaderProgram = delegate.toGlShaderProgram(context, useHdr)

    private companion object {
        const val BLUR_SIGMA_PIXELS = 20f
        const val SHARP_FRAME_SCALE_X = 0.74f
        const val SHARP_FRAME_SCALE_Y = 1f
    }
}

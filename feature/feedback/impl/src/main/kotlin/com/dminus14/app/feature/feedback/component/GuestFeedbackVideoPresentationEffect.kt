package com.dminus14.app.feature.feedback.component

import android.content.Context
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.GaussianBlurWithFrameOverlaid
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

/** 같은 영상 프레임으로 흐린 배경과 중앙의 선명한 전경을 합성하는 Feedback 전용 효과다. */
@UnstableApi
internal class GuestFeedbackVideoPresentationEffect(
    sharpFrameScaleX: Float,
    sharpFrameScaleY: Float,
) : GlEffect {
    private val delegate =
        GaussianBlurWithFrameOverlaid(
            BLUR_SIGMA_PIXELS,
            sharpFrameScaleX,
            sharpFrameScaleY,
        )

    @Throws(VideoFrameProcessingException::class)
    override fun toGlShaderProgram(
        context: Context,
        useHdr: Boolean,
    ): GlShaderProgram = delegate.toGlShaderProgram(context, useHdr)

    private companion object {
        const val BLUR_SIGMA_PIXELS = 20f
    }
}

/** 입력 영상의 종횡비를 보존하면서 출력 영역 안에 들어가는 정규화 축 배율을 계산한다. */
internal fun calculateSharpFrameScale(
    inputWidth: Int,
    inputHeight: Int,
    outputWidth: Int,
    outputHeight: Int,
): SharpFrameScale {
    if (minOf(inputWidth, inputHeight, outputWidth, outputHeight) <= 0) {
        return SharpFrameScale(x = 1f, y = 1f)
    }

    val inputAspectRatio = inputWidth.toFloat() / inputHeight
    val outputAspectRatio = outputWidth.toFloat() / outputHeight
    return if (inputAspectRatio > outputAspectRatio) {
        SharpFrameScale(x = 1f, y = outputAspectRatio / inputAspectRatio)
    } else {
        SharpFrameScale(x = inputAspectRatio / outputAspectRatio, y = 1f)
    }
}

internal data class SharpFrameScale(
    val x: Float,
    val y: Float,
)

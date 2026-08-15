package com.dminus14.app.feature.interview.media

import androidx.camera.video.Quality

/**
 * 인터뷰 영상 녹화 스펙(화질, 비트레이트 등)을 한 곳에서 관리.
 */
object InterviewVideoRecordingSpec {
    val QUALITY: Quality = Quality.SD
    const val TARGET_VIDEO_BITRATE: Int = 2_000_000
}

package com.dminus14.app.feature.interview.media

import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import java.io.File

interface InterviewVideoRecorder {
    val videoCapture: VideoCapture<Recorder>

    fun start(
        outputFile: File,
        onEvent: (InterviewRecordingEvent) -> Unit,
    )

    fun pause()

    fun resume()

    fun stop()
}

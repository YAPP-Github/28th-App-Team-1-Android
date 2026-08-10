package com.dminus14.app.feature.interview.media

interface InterviewAudioPlayer {
    fun play(
        request: InterviewAudioPlaybackRequest,
        listener: Listener,
    )

    fun stop()

    interface Listener {
        fun onStarted()

        fun onCompleted()

        fun onFailure(cause: Throwable)
    }
}

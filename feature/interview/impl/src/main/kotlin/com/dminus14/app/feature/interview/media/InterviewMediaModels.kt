package com.dminus14.app.feature.interview.media

sealed interface InterviewAudioPlaybackRequest {
    data class AuthenticatedQuestionStream(
        val url: String,
    ) : InterviewAudioPlaybackRequest

    data class Base64WrapUpMp3(
        val payload: String,
    ) : InterviewAudioPlaybackRequest
}

enum class InterviewAudioState { ACTIVE, INACTIVE, ERROR }

data class InterviewAudioLevelSample(
    val timestampMillis: Long,
    val amplitude: Double,
    val state: InterviewAudioState,
)

sealed interface InterviewSpeechDetectionEvent {
    data object SpeechStarted : InterviewSpeechDetectionEvent

    data object SilenceElapsed : InterviewSpeechDetectionEvent

    data object MicrophoneFailure : InterviewSpeechDetectionEvent
}

sealed interface InterviewRecordingEvent {
    data object Started : InterviewRecordingEvent

    data class AudioLevel(
        val sample: InterviewAudioLevelSample,
    ) : InterviewRecordingEvent

    data object Finalized : InterviewRecordingEvent

    data class Failed(
        val cause: Throwable,
    ) : InterviewRecordingEvent
}

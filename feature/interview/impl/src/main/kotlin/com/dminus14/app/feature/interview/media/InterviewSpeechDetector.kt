package com.dminus14.app.feature.interview.media

import com.dminus14.app.feature.interview.InterviewConstants
import javax.inject.Inject

class InterviewSpeechDetector
    @Inject
    constructor() {
        private var consecutiveSpeechSamples = 0
        private var hasSpeechStarted = false
        private var silenceStartedAtMillis: Long? = null
        private var hasSilenceElapsed = false

        @Suppress("ReturnCount")
        fun accept(sample: InterviewAudioLevelSample): InterviewSpeechDetectionEvent? {
            if (sample.state != InterviewAudioState.ACTIVE) {
                return InterviewSpeechDetectionEvent.MicrophoneFailure
            }
            if (!hasSpeechStarted) {
                consecutiveSpeechSamples =
                    if (sample.amplitude >= InterviewConstants.SPEECH_START_AMPLITUDE) {
                        consecutiveSpeechSamples + 1
                    } else {
                        0
                    }
                if (consecutiveSpeechSamples >= InterviewConstants.SPEECH_START_SAMPLE_COUNT) {
                    hasSpeechStarted = true
                    return InterviewSpeechDetectionEvent.SpeechStarted
                }
                return null
            }
            if (hasSilenceElapsed) return null
            if (sample.amplitude <= InterviewConstants.SILENCE_AMPLITUDE) {
                val startedAt =
                    silenceStartedAtMillis
                        ?: sample.timestampMillis.also { silenceStartedAtMillis = it }
                val silenceDurationMillis = sample.timestampMillis - startedAt
                if (silenceDurationMillis >= InterviewConstants.ANSWER_SILENCE_MILLIS) {
                    hasSilenceElapsed = true
                    return InterviewSpeechDetectionEvent.SilenceElapsed
                }
            } else {
                silenceStartedAtMillis = null
            }
            return null
        }

        fun reset() {
            consecutiveSpeechSamples = 0
            hasSpeechStarted = false
            silenceStartedAtMillis = null
            hasSilenceElapsed = false
        }
    }

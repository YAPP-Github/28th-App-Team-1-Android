package com.dminus14.app.domain.model

enum class InterviewMediaSegmentType { QUESTION_VIDEO, ANSWER_VIDEO, ANSWER_AUDIO }

enum class InterviewMediaFinalizeState { WRITING, FINALIZED }

data class InterviewMediaSegment(
    val sequence: Int,
    val type: InterviewMediaSegmentType,
    val mediaRef: InterviewMediaFileRef,
    val questionId: Long?,
    val startedAtMillis: Long,
    val endedAtMillis: Long?,
    val gapBeforeMillis: Long,
    val finalizeState: InterviewMediaFinalizeState,
)

data class InterviewVideoSettings(
    val width: Int = 854,
    val height: Int = 480,
    val framesPerSecond: Int = 30,
    val videoBitrate: Int,
    val videoMimeType: String = "video/avc",
    val audioMimeType: String = "audio/mp4a-latm",
)

data class InterviewWrapUpRange(
    val startMillis: Long,
    val endMillis: Long,
)

data class InterviewMediaManifest(
    val schemaVersion: Int = SCHEMA_VERSION,
    val sessionId: Long,
    val nextSequence: Int = 0,
    val currentQuestionId: Long? = null,
    val pendingAnswer: SubmitInterviewAnswerCommand? = null,
    val videoSettings: InterviewVideoSettings? = null,
    val segments: List<InterviewMediaSegment> = emptyList(),
    val wrapUpRange: InterviewWrapUpRange? = null,
) {
    companion object {
        const val SCHEMA_VERSION = 1
    }
}

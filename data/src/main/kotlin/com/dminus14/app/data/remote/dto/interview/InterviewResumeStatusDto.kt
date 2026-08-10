@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewResumeState
import com.dminus14.app.domain.model.InterviewResumeStatus
import com.dminus14.app.domain.model.InterviewTerminalStatus
import com.google.gson.annotations.SerializedName

/** GET api/v1/interview/sessions/{sessionId}/resume */
data class InterviewResumeStatusResponseDto(
    @SerializedName("resumeState")
    val resumeState: String,
    @SerializedName("startedAt")
    val startedAt: String?,
    @SerializedName("elapsedSeconds")
    val elapsedSeconds: Long?,
    @SerializedName("status")
    val status: String?,
) {
    fun toDomain(): InterviewResumeStatus =
        InterviewResumeStatus(
            resumeState = resumeState.toInterviewResumeState(),
            startedAt = startedAt,
            elapsedSeconds = elapsedSeconds,
            status = status?.toInterviewTerminalStatus(),
        )
}

internal fun String.toInterviewResumeState(): InterviewResumeState =
    when (this) {
        "RESUMABLE" -> InterviewResumeState.Resumable
        "ENDED" -> InterviewResumeState.Ended
        else -> InterviewResumeState.Unknown(this)
    }

internal fun String.toInterviewTerminalStatus(): InterviewTerminalStatus =
    when (this) {
        "COMPLETED" -> InterviewTerminalStatus.Completed
        "ABANDONED" -> InterviewTerminalStatus.Abandoned
        "INVALID" -> InterviewTerminalStatus.Invalid
        else -> InterviewTerminalStatus.Unknown(this)
    }

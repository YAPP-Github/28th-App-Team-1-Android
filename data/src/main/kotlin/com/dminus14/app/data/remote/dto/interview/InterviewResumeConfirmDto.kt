@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewAbandonCause
import com.dminus14.app.domain.model.InterviewResumeConfirm
import com.google.gson.annotations.SerializedName

/** POST api/v1/interview/sessions/{sessionId}/resume */
data class InterviewResumeConfirmResponseDto(
    @SerializedName("nextQuestion")
    val nextQuestion: NextQuestionDto?,
    @SerializedName("sessionEnded")
    val sessionEnded: Boolean,
    @SerializedName("wrapUpMessage")
    val wrapUpMessage: WrapUpMessageDto?,
    @SerializedName("endType")
    val endType: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("abandonCause")
    val abandonCause: String?,
    @SerializedName("endedAt")
    val endedAt: String?,
) {
    fun toDomain(): InterviewResumeConfirm =
        InterviewResumeConfirm(
            nextQuestion = nextQuestion?.toDomain(),
            sessionEnded = sessionEnded,
            wrapUpMessage = wrapUpMessage?.toDomain(),
            endType = endType?.toInterviewEndType(),
            status = status?.toInterviewTerminalStatus(),
            abandonCause = abandonCause?.toInterviewAbandonCause(),
            endedAt = endedAt,
        )
}

internal fun String.toInterviewAbandonCause(): InterviewAbandonCause =
    when (this) {
        "NETWORK_DISCONNECT" -> InterviewAbandonCause.NetworkDisconnect
        "USER_EXIT" -> InterviewAbandonCause.UserExit
        "HOLD_EXPIRED" -> InterviewAbandonCause.HoldExpired
        else -> InterviewAbandonCause.Unknown(this)
    }

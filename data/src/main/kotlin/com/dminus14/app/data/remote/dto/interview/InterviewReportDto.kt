package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.AttitudeRating
import com.dminus14.app.domain.model.GuestFeedbackItem
import com.dminus14.app.domain.model.GuestFeedbackSummary
import com.dminus14.app.domain.model.HighlightReason
import com.dminus14.app.domain.model.HighlightSpan
import com.dminus14.app.domain.model.HighlightTone
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportCard
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.model.InterviewReportVideo
import com.dminus14.app.domain.model.ReportScript
import com.dminus14.app.domain.model.ScriptRole
import com.dminus14.app.domain.model.ScriptSegment
import com.google.gson.annotations.SerializedName

/** GET api/v1/interview/sessions/{sessionId}/report */
data class InterviewReportResponseDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("headline")
    val headline: String?,
    @SerializedName("video")
    val video: InterviewReportVideoDto?,
    @SerializedName("cards")
    val cards: List<InterviewReportCardDto>?,
    @SerializedName("script")
    val script: List<ReportScriptDto>?,
    @SerializedName("guestFeedback")
    val guestFeedback: GuestFeedbackSummaryDto?,
) {
    fun toDomain(): InterviewReport =
        InterviewReport(
            status = InterviewReportStatus.fromRaw(status),
            headline = headline,
            video = video?.toDomain(),
            cards = cards?.map { it.toDomain() },
            script = script?.map { it.toDomain() },
            guestFeedback = guestFeedback?.toDomain(),
        )
}

data class InterviewReportVideoDto(
    @SerializedName("url")
    val url: String?,
    @SerializedName("expired")
    val expired: Boolean,
    @SerializedName("expiresAt")
    val expiresAt: String?,
) {
    fun toDomain(): InterviewReportVideo =
        InterviewReportVideo(
            url = url,
            expired = expired,
            expiresAt = expiresAt,
        )
}

data class InterviewReportCardDto(
    @SerializedName("axisOrder")
    val axisOrder: Int,
    @SerializedName("depthLevel")
    val depthLevel: Int,
    @SerializedName("questionText")
    val questionText: String,
    @SerializedName("transcript")
    val transcript: String,
    @SerializedName("highlightSpans")
    val highlightSpans: List<HighlightSpanDto>,
    @SerializedName("resolutionNotice")
    val resolutionNotice: String?,
    @SerializedName("cardRedFlagNotices")
    val cardRedFlagNotices: List<String>?,
    @SerializedName("questionIntentTitle")
    val questionIntentTitle: String?,
    @SerializedName("questionIntent")
    val questionIntent: String?,
    @SerializedName("scriptSegments")
    val scriptSegments: List<ScriptSegmentDto>?,
) {
    fun toDomain(): InterviewReportCard =
        InterviewReportCard(
            axisOrder = axisOrder,
            depthLevel = depthLevel,
            questionText = questionText,
            transcript = transcript,
            highlightSpans = highlightSpans.map { it.toDomain() },
            resolutionNotice = resolutionNotice,
            cardRedFlagNotices = cardRedFlagNotices,
            questionIntentTitle = questionIntentTitle,
            questionIntent = questionIntent,
            scriptSegments = scriptSegments?.map { it.toDomain() },
        )
}

data class HighlightSpanDto(
    @SerializedName("startIndex")
    val startIndex: Int,
    @SerializedName("endIndex")
    val endIndex: Int,
    @SerializedName("tone")
    val tone: String,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("analysis")
    val analysis: String,
    @SerializedName("followUpQuestions")
    val followUpQuestions: List<String>,
    @SerializedName("startSec")
    val startSec: Float?,
    @SerializedName("answerTopicTitle")
    val answerTopicTitle: String?,
    @SerializedName("questionIntentTitle")
    val questionIntentTitle: String?,
    @SerializedName("questionIntent")
    val questionIntent: String?,
) {
    fun toDomain(): HighlightSpan =
        HighlightSpan(
            startIndex = startIndex,
            endIndex = endIndex,
            tone = HighlightTone.fromRaw(tone),
            reason = HighlightReason.fromRaw(reason),
            title = title,
            analysis = analysis,
            followUpQuestions = followUpQuestions,
            startSec = startSec,
            answerTopicTitle = answerTopicTitle,
            questionIntentTitle = questionIntentTitle,
            questionIntent = questionIntent,
        )
}

data class ScriptSegmentDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("startIndex")
    val startIndex: Int?,
    @SerializedName("endIndex")
    val endIndex: Int?,
    @SerializedName("startSec")
    val startSec: Float?,
    @SerializedName("endSec")
    val endSec: Float?,
) {
    fun toDomain(): ScriptSegment =
        ScriptSegment(
            role = ScriptRole.fromRaw(role),
            text = text,
            startIndex = startIndex,
            endIndex = endIndex,
            startSec = startSec,
            endSec = endSec,
        )
}

data class ReportScriptDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("startSec")
    val startSec: Float,
    @SerializedName("endSec")
    val endSec: Float,
) {
    fun toDomain(): ReportScript =
        ReportScript(
            role = ScriptRole.fromRaw(role),
            text = text,
            startSec = startSec,
            endSec = endSec,
        )
}

data class GuestFeedbackSummaryDto(
    @SerializedName("participantCount")
    val participantCount: Int,
    @SerializedName("guests")
    val guests: List<GuestFeedbackItemDto>,
) {
    fun toDomain(): GuestFeedbackSummary =
        GuestFeedbackSummary(
            participantCount = participantCount,
            guests = guests.map { it.toDomain() },
        )
}

data class GuestFeedbackItemDto(
    @SerializedName("alias")
    val alias: String,
    @SerializedName("attitudeRatings")
    val attitudeRatings: List<AttitudeRatingDto>,
) {
    fun toDomain(): GuestFeedbackItem =
        GuestFeedbackItem(
            alias = alias,
            attitudeRatings = attitudeRatings.map { it.toDomain() },
        )
}

data class AttitudeRatingDto(
    @SerializedName("axis")
    val axis: String,
    @SerializedName("level")
    val level: Int,
    @SerializedName("comment")
    val comment: String?,
) {
    fun toDomain(): AttitudeRating =
        AttitudeRating(
            axis = axis,
            level = level,
            comment = comment,
        )
}

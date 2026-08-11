package com.dminus14.app.domain.model

enum class InterviewReportStatus {
    GENERATING,
    READY,
    INSUFFICIENT_ANALYSIS,
    FAILED,
    UNKNOWN,
    ;

    companion object {
        fun fromRaw(rawStatus: String): InterviewReportStatus =
            entries.firstOrNull { status -> status.name.equals(rawStatus, ignoreCase = true) }
                ?: UNKNOWN
    }
}

data class InterviewReportList(
    val reports: List<InterviewReportListItem>,
)

data class InterviewReportListItem(
    val sessionId: Long,
    val jobType: String?,
    val jobTypeLabel: String?,
    val careerYears: Int?,
    val interviewedAt: String?,
    val portfolioFileName: String?,
    val portfolioDeleted: Boolean,
    val jdUrl: String?,
    val reportStatus: InterviewReportStatus,
    val feedbackAvailable: Boolean,
    val title: String? = null,
)

data class InterviewReport(
    val status: InterviewReportStatus,
    val headline: String?,
    val video: InterviewReportVideo?,
    val cards: List<InterviewReportCard>?,
    val script: List<ReportScript>?,
    val guestFeedback: GuestFeedbackSummary?,
)

data class InterviewReportVideo(
    val url: String?,
    val expired: Boolean,
    val expiresAt: String?,
)

data class InterviewReportCard(
    val axisOrder: Int,
    val depthLevel: Int,
    val questionText: String,
    val transcript: String,
    val highlightSpans: List<HighlightSpan>,
    val resolutionNotice: String?,
    val cardRedFlagNotices: List<String>?,
    val questionIntentTitle: String?,
    val questionIntent: String?,
    val scriptSegments: List<ScriptSegment>?,
)

data class HighlightSpan(
    val startIndex: Int,
    val endIndex: Int,
    val tone: String,
    val reason: String,
    val title: String,
    val analysis: String,
    val followUpQuestions: List<String>,
    val startSec: Float?,
    val answerTopicTitle: String?,
    val questionIntentTitle: String?,
    val questionIntent: String?,
)

data class ScriptSegment(
    val role: String,
    val text: String,
    val startIndex: Int?,
    val endIndex: Int?,
    val startSec: Float?,
    val endSec: Float?,
)

data class ReportScript(
    val role: String,
    val text: String,
    val startSec: Float,
    val endSec: Float,
)

data class GuestFeedbackSummary(
    val participantCount: Int,
    val guests: List<GuestFeedbackItem>,
)

data class GuestFeedbackItem(
    val alias: String,
    val attitudeRatings: List<AttitudeRating>,
)

data class AttitudeRating(
    val axis: String,
    val level: Int,
    val comment: String?,
)

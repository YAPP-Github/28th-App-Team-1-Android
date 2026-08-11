package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.InterviewReportList
import com.dminus14.app.domain.model.InterviewReportListItem
import com.dminus14.app.domain.model.InterviewReportStatus
import com.google.gson.annotations.SerializedName

/** GET api/v1/interview/sessions */
data class InterviewReportListResponseDto(
    @SerializedName("reports")
    val reports: List<InterviewReportListItemDto>,
) {
    fun toDomain(): InterviewReportList =
        InterviewReportList(
            reports = reports.map { it.toDomain() },
        )
}

data class InterviewReportListItemDto(
    @SerializedName("sessionId")
    val sessionId: Long,
    @SerializedName("jobType")
    val jobType: String?,
    @SerializedName("jobTypeLabel")
    val jobTypeLabel: String?,
    @SerializedName("careerYears")
    val careerYears: Int?,
    @SerializedName("interviewedAt")
    val interviewedAt: String?,
    @SerializedName("portfolioFileName")
    val portfolioFileName: String?,
    @SerializedName("portfolioDeleted")
    val portfolioDeleted: Boolean,
    @SerializedName("jdUrl")
    val jdUrl: String?,
    @SerializedName("reportStatus")
    val reportStatus: String,
    @SerializedName("feedbackAvailable")
    val feedbackAvailable: Boolean,
    @SerializedName("title")
    val title: String?,
) {
    fun toDomain(): InterviewReportListItem =
        InterviewReportListItem(
            sessionId = sessionId,
            jobType = jobType,
            jobTypeLabel = jobTypeLabel,
            careerYears = careerYears,
            interviewedAt = interviewedAt,
            portfolioFileName = portfolioFileName,
            portfolioDeleted = portfolioDeleted,
            jdUrl = jdUrl,
            reportStatus = InterviewReportStatus.fromRaw(reportStatus),
            feedbackAvailable = feedbackAvailable,
            title = title,
        )
}

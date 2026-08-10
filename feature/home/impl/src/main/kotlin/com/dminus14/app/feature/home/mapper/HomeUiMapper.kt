package com.dminus14.app.feature.home.mapper

import com.dminus14.app.domain.model.InterviewReportListItem
import com.dminus14.app.feature.home.HomeReportItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 도메인 면접 리포트 리스트 항목을 홈 카드 모델로 변환하는 규칙을 모은다.
 */

private val HomeReportDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("M월 d일 E", Locale.KOREAN)

internal fun InterviewReportListItem.toHomeReportItem(): HomeReportItem =
    HomeReportItem(
        id = sessionId.toString(),
        date = formatInterviewedAt(interviewedAt),
        title = title,
    )

/**
 * 서버의 `interviewedAt`(ISO local date-time, 예: `2026-07-02T14:20:00`)을 시안 형식
 * (`7월 2일 목`)으로 변환한다. 값이 없거나 형식이 예상과 다르면 빈 문자열로 둔다.
 */
internal fun formatInterviewedAt(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return runCatching { LocalDateTime.parse(raw).format(HomeReportDateFormatter) }
        .getOrDefault("")
}

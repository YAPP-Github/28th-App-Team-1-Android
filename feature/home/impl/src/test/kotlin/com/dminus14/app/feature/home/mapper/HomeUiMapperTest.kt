package com.dminus14.app.feature.home.mapper

import com.dminus14.app.domain.model.InterviewReportListItem
import com.dminus14.app.domain.model.InterviewReportStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeUiMapperTest {
    // ---- formatInterviewedAt ----

    @Test
    fun `interviewedAt이 null이면 빈 문자열을 반환한다`() {
        assertEquals("", formatInterviewedAt(null))
    }

    @Test
    fun `interviewedAt이 빈 문자열이면 빈 문자열을 반환한다`() {
        assertEquals("", formatInterviewedAt(""))
    }

    @Test
    fun `interviewedAt이 공백 문자열이면 빈 문자열을 반환한다`() {
        assertEquals("", formatInterviewedAt("   "))
    }

    @Test
    fun `interviewedAt이 유효한 ISO local date-time이면 시안 형식으로 변환된다`() {
        // 2026-07-02 는 목요일.
        assertEquals("7월 2일 목", formatInterviewedAt("2026-07-02T14:20:00"))
    }

    @Test
    fun `interviewedAt이 잘못된 포맷이면 예외를 삼키고 빈 문자열을 반환한다`() {
        // ISO 아닌 임의의 문자열은 LocalDateTime 파싱에서 실패한다.
        assertEquals("", formatInterviewedAt("not-a-date"))
        assertEquals("", formatInterviewedAt("2026/07/02 14:20:00"))
    }

    @Test
    fun `interviewedAt이 date만 있고 시간 부분이 없어도 파싱 실패 시 빈 문자열이 된다`() {
        // LocalDateTime 은 시간부까지 요구하므로 date-only 는 실패한다.
        assertEquals("", formatInterviewedAt("2026-07-02"))
    }

    // ---- toHomeReportItem ----

    @Test
    fun `toHomeReportItem 은 sessionId를 문자열 id로 title date를 함께 매핑한다`() {
        val source =
            sampleReportItem(
                sessionId = 42L,
                interviewedAt = "2026-07-02T14:20:00",
                title = "인터뷰 리포트 제목",
            )

        val item = source.toHomeReportItem()

        assertEquals("42", item.id)
        assertEquals("7월 2일 목", item.date)
        assertEquals("인터뷰 리포트 제목", item.title)
    }

    @Test
    fun `toHomeReportItem 은 title이 null이어도 그대로 null로 전달한다`() {
        val item = sampleReportItem(title = null).toHomeReportItem()

        assertEquals(null, item.title)
    }

    @Test
    fun `toHomeReportItem 은 interviewedAt이 없거나 잘못된 포맷이면 date를 빈 문자열로 매핑한다`() {
        assertEquals("", sampleReportItem(interviewedAt = null).toHomeReportItem().date)
        assertEquals("", sampleReportItem(interviewedAt = "bad").toHomeReportItem().date)
    }

    private fun sampleReportItem(
        sessionId: Long = 1L,
        interviewedAt: String? = "2026-07-02T14:20:00",
        title: String? = "샘플",
    ): InterviewReportListItem =
        InterviewReportListItem(
            sessionId = sessionId,
            jobType = "ANDROID",
            jobTypeLabel = "Android",
            careerYears = 3,
            interviewedAt = interviewedAt,
            portfolioFileName = null,
            portfolioDeleted = false,
            jdUrl = null,
            reportStatus = InterviewReportStatus.READY,
            feedbackAvailable = true,
            title = title,
        )
}

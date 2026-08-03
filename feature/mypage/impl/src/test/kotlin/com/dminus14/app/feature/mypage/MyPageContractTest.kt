package com.dminus14.app.feature.mypage

import org.junit.Assert.assertEquals
import org.junit.Test

class MyPageContractTest {
    @Test
    fun `직군과 연차로 레포트의 표시 문자열을 계산한다`() {
        val report =
            MyPageReportUiModel(
                id = "synthetic-report",
                jobRole = MyPageJobRole.Ios,
                experienceYears = 2,
                createdAt = "2026.08.04",
                status = MyPageReportStatus.Completed,
                portfolioFileName = "sample_portfolio.pdf",
                jobDescription = "JD 직접 입력",
            )

        assertEquals("iOS", MyPageJobRole.Ios.toString())
        assertEquals("iOS · 2년", report.jobRoleAndExperience)
    }
}

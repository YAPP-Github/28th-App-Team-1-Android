package com.dminus14.app.data.remote.dto

import com.dminus14.app.data.di.remote.network.GsonModule
import com.dminus14.app.domain.model.PortfolioStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PortfolioListResponseDtoTest {
    private val gson = GsonModule.provideGson()

    @Test
    fun `계정 정책 필드가 있으면 그대로 역직렬화한다`() {
        val json =
            """
            {
              "portfolios": [],
              "replaceAvailable": false,
              "nextAvailableAt": "2026-09-01T00:00:00",
              "deleteAvailable": false,
              "nextDeleteAvailableAt": "2026-09-01T00:00:00"
            }
            """.trimIndent()

        val dto = gson.fromJson(json, PortfolioListResponseDto::class.java)

        assertFalse(dto.replaceAvailable!!)
        assertEquals("2026-09-01T00:00:00", dto.nextAvailableAt)
        assertFalse(dto.deleteAvailable!!)
        assertEquals("2026-09-01T00:00:00", dto.nextDeleteAvailableAt)
    }

    @Test
    fun `계정 정책 필드가 없으면 기본값을 사용한다`() {
        val json = """{ "portfolios": [] }"""

        val dto = gson.fromJson(json, PortfolioListResponseDto::class.java)
        val overview = dto.toDomain()

        assertNull(dto.replaceAvailable)
        assertNull(dto.nextAvailableAt)
        assertNull(dto.deleteAvailable)
        assertNull(dto.nextDeleteAvailableAt)
        assertTrue(overview.isReplaceAvailable)
        assertTrue(overview.isDeleteAvailable)
    }

    @Test
    fun `처리 중 상태는 uploadedAt이 null이어도 역직렬화된다`() {
        val json =
            """
            {
              "portfolioId": "portfolio-1",
              "fileName": "resume.pdf",
              "fileSize": 1048576,
              "pageCount": 12,
              "status": "PROCESSING",
              "uploadedAt": null,
              "interviewInProgress": false
            }
            """.trimIndent()

        val dto = gson.fromJson(json, PortfolioDto::class.java)

        assertNull(dto.uploadedAt)
        assertEquals(PortfolioStatus.PROCESSING, dto.toDomain().status)
    }

    @Test
    fun `interviewInProgress가 true면 진행 중 면접으로 매핑된다`() {
        val json =
            """
            {
              "portfolioId": "portfolio-1",
              "fileName": "resume.pdf",
              "fileSize": 1048576,
              "pageCount": 12,
              "status": "READY",
              "uploadedAt": "2026-07-01T10:00:00",
              "interviewInProgress": true
            }
            """.trimIndent()

        val dto = gson.fromJson(json, PortfolioDto::class.java)

        assertTrue(dto.toDomain().isInterviewInProgress)
    }

    @Test
    fun `목록 응답을 개요 도메인 모델로 변환한다`() {
        val json =
            """
            {
              "portfolios": [
                {
                  "portfolioId": "portfolio-1",
                  "fileName": "resume.pdf",
                  "fileSize": 1048576,
                  "pageCount": 12,
                  "status": "READY",
                  "uploadedAt": "2026-07-01T10:00:00",
                  "interviewInProgress": false
                }
              ],
              "replaceAvailable": true,
              "nextAvailableAt": null,
              "deleteAvailable": true,
              "nextDeleteAvailableAt": null
            }
            """.trimIndent()

        val overview = gson.fromJson(json, PortfolioListResponseDto::class.java).toDomain()

        assertEquals("portfolio-1", overview.portfolio?.portfolioId)
        assertTrue(overview.isReplaceAvailable)
        assertTrue(overview.isDeleteAvailable)
    }
}

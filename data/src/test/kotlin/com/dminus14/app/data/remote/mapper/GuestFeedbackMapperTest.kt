package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackAxisDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackGateDto
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackRating
import com.dminus14.app.domain.model.GuestFeedbackUnavailableReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GuestFeedbackMapperTest {
    @Test
    fun `OPEN 응답의 모든 진입 데이터를 도메인 모델로 변환한다`() {
        val response =
            GuestFeedbackEntryResponseDto(
                gate = GuestFeedbackGateDto.OPEN,
                requesterName = "합성 요청자",
                axes =
                    GuestFeedbackAxisCodeDto.entries.map { code ->
                        GuestFeedbackAxisDto(code, "합성 표시명 ${code.name}")
                    },
                videoUrl = "https://example.invalid/synthetic-video",
                submissionOpen = true,
            )

        val actual = response.toDomain()

        assertTrue(actual is GuestFeedbackEntry.Open)
        actual as GuestFeedbackEntry.Open
        assertEquals("합성 요청자", actual.requesterName)
        assertEquals(GuestFeedbackAxisCode.entries, actual.axes.map { axis -> axis.code })
        assertEquals("https://example.invalid/synthetic-video", actual.videoUrl)
        assertTrue(actual.submissionOpen)
    }

    @Test
    fun `non OPEN 게이트를 각각 작성 불가 사유로 변환한다`() {
        val cases =
            mapOf(
                GuestFeedbackGateDto.PRIVATE to GuestFeedbackUnavailableReason.PRIVATE,
                GuestFeedbackGateDto.EXPIRED to GuestFeedbackUnavailableReason.EXPIRED,
                GuestFeedbackGateDto.FULL to GuestFeedbackUnavailableReason.FULL,
                GuestFeedbackGateDto.ALREADY_SUBMITTED to
                    GuestFeedbackUnavailableReason.ALREADY_SUBMITTED,
            )

        cases.forEach { (gate, reason) ->
            val actual = closedResponse(gate).toDomain()

            assertEquals(GuestFeedbackEntry.Unavailable(reason), actual)
        }
    }

    @Test
    fun `모든 도메인 평가 축을 대응하는 wire DTO로 변환한다`() {
        val actual =
            GuestFeedbackAxisCode.entries.map { axis ->
                GuestFeedbackRating(
                    axis = axis,
                    level = 3,
                    comment = "",
                ).toDto()
            }

        assertEquals(GuestFeedbackAxisCodeDto.entries, actual.map { rating -> rating.axis })
        assertTrue(actual.all { rating -> rating.level == 3 })
        assertTrue(actual.all { rating -> rating.comment == "" })
    }

    private fun closedResponse(gate: GuestFeedbackGateDto) =
        GuestFeedbackEntryResponseDto(
            gate = gate,
            requesterName = null,
            axes = null,
            videoUrl = null,
            submissionOpen = null,
        )
}

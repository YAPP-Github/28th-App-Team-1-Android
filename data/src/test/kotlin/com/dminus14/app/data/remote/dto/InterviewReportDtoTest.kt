package com.dminus14.app.data.remote.dto

import com.dminus14.app.data.remote.dto.interview.HighlightSpanDto
import com.dminus14.app.data.remote.dto.interview.ReportScriptDto
import com.dminus14.app.data.remote.dto.interview.ScriptSegmentDto
import com.dminus14.app.domain.model.HighlightReason
import com.dminus14.app.domain.model.HighlightTone
import com.dminus14.app.domain.model.ScriptRole
import org.junit.Assert.assertEquals
import org.junit.Test

class InterviewReportDtoTest {
    @Test
    fun `HighlightSpanDto 는 tone 과 reason 을 enum 으로 변환한다`() {
        val actual = sampleHighlightDto(tone = "POSITIVE", reason = "PROBE_WORTHY").toDomain()

        assertEquals(HighlightTone.POSITIVE, actual.tone)
        assertEquals(HighlightReason.PROBE_WORTHY, actual.reason)
    }

    @Test
    fun `알 수 없는 tone 과 reason 은 UNKNOWN 으로 변환한다`() {
        val actual = sampleHighlightDto(tone = "MAGENTA", reason = "MYSTERY").toDomain()

        assertEquals(HighlightTone.UNKNOWN, actual.tone)
        assertEquals(HighlightReason.UNKNOWN, actual.reason)
    }

    @Test
    fun `ScriptSegmentDto 는 role 을 enum 으로 변환한다`() {
        val actual =
            ScriptSegmentDto(
                role = "INTERVIEWER",
                text = "안녕하세요.",
                startIndex = null,
                endIndex = null,
                startSec = null,
                endSec = null,
            ).toDomain()

        assertEquals(ScriptRole.INTERVIEWER, actual.role)
    }

    @Test
    fun `알 수 없는 ScriptSegmentDto role 은 UNKNOWN 으로 변환한다`() {
        val actual =
            ScriptSegmentDto(
                role = "OBSERVER",
                text = "관찰합니다.",
                startIndex = null,
                endIndex = null,
                startSec = null,
                endSec = null,
            ).toDomain()

        assertEquals(ScriptRole.UNKNOWN, actual.role)
    }

    @Test
    fun `ReportScriptDto 는 role 을 enum 으로 변환한다`() {
        val actual =
            ReportScriptDto(
                role = "INTERVIEWEE",
                text = "그 프로젝트에서 캐시를 도입했어요.",
                startSec = 12.5f,
                endSec = 18.0f,
            ).toDomain()

        assertEquals(ScriptRole.INTERVIEWEE, actual.role)
    }

    private fun sampleHighlightDto(
        tone: String,
        reason: String,
    ): HighlightSpanDto =
        HighlightSpanDto(
            startIndex = 0,
            endIndex = 10,
            tone = tone,
            reason = reason,
            title = "구체적인 사례 제시",
            analysis = "사례가 구체적이었어요.",
            followUpQuestions = emptyList(),
            startSec = null,
            answerTopicTitle = null,
            questionIntentTitle = null,
            questionIntent = null,
        )
}

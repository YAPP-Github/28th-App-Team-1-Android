package com.dminus14.app.data.remote.serialization

import com.dminus14.app.data.di.remote.network.GsonModule
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackGateDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitRequestDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitResponseDto
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/** Guest Gson이 공용 JSON 정책과 Guest Feedback 응답 계약을 함께 보장하는지 검증한다. */
class GuestFeedbackSerializationTest {
    private val gson = GsonModule.provideGuestGson(GsonModule.provideGson())

    @Test
    fun `OPEN 응답의 필수 키와 확정 axis 열거형을 역직렬화한다`() {
        val response = gson.fromJson(OPEN_RESPONSE, GuestFeedbackEntryResponseDto::class.java)

        assertEquals(GuestFeedbackGateDto.OPEN, response.gate)
        val axis = requireNotNull(response.axes).single()
        assertEquals(GuestFeedbackAxisCodeDto.GESTURE, axis.code)
        assertEquals("손동작", axis.displayName)
    }

    @Test
    fun `non-OPEN 응답은 네 키가 명시적 null일 때만 허용한다`() {
        val response = gson.fromJson(CLOSED_RESPONSE, GuestFeedbackEntryResponseDto::class.java)

        assertEquals(GuestFeedbackGateDto.FULL, response.gate)
        assertNull(response.requesterName)
        assertNull(response.axes)
        assertNull(response.videoUrl)
        assertNull(response.submissionOpen)
    }

    @Test
    fun `진입 응답에서 필수 키가 누락되면 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(MISSING_VIDEO_URL_RESPONSE, GuestFeedbackEntryResponseDto::class.java)
        }
    }

    @Test
    fun `OPEN 응답의 필수 값이 null이면 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(OPEN_NULL_RESPONSE, GuestFeedbackEntryResponseDto::class.java)
        }
    }

    @Test
    fun `non-OPEN 응답에 진입 값이 있으면 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(CLOSED_WITH_DATA_RESPONSE, GuestFeedbackEntryResponseDto::class.java)
        }
    }

    @Test
    fun `지원하지 않는 gate와 axis 값은 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                OPEN_RESPONSE.replace("OPEN", "SYNTHETIC_GATE"),
                GuestFeedbackEntryResponseDto::class.java,
            )
        }
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                OPEN_RESPONSE.replace("GESTURE", "SYNTHETIC_AXIS"),
                GuestFeedbackEntryResponseDto::class.java,
            )
        }
    }

    @Test
    fun `OPEN 응답의 axis 원소나 필수 필드가 null이면 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                OPEN_RESPONSE.replace(
                    """{"code":"GESTURE","displayName":"손동작"}""",
                    "null",
                ),
                GuestFeedbackEntryResponseDto::class.java,
            )
        }
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                OPEN_RESPONSE.replace("\"code\":\"GESTURE\"", "\"code\":null"),
                GuestFeedbackEntryResponseDto::class.java,
            )
        }
    }

    @Test
    fun `UTC Z 시각은 Instant로 파싱한다`() {
        val response = gson.fromJson(SUBMIT_RESPONSE, GuestFeedbackSubmitResponseDto::class.java)

        assertEquals(Instant.parse(SUBMITTED_AT), response.submittedAt)
    }

    @Test
    fun `UTC Z가 아닌 시각과 잘못된 시각은 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                SUBMIT_RESPONSE.replace(SUBMITTED_AT, "2026-07-30T18:58:13.348+09:00"),
                GuestFeedbackSubmitResponseDto::class.java,
            )
        }
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                SUBMIT_RESPONSE.replace(SUBMITTED_AT, "synthetic-invalid-time"),
                GuestFeedbackSubmitResponseDto::class.java,
            )
        }
    }

    @Test
    fun `제출 응답의 ID나 시각이 누락 또는 null이면 실패한다`() {
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                SUBMIT_RESPONSE.replace("\"submissionId\":41,", ""),
                GuestFeedbackSubmitResponseDto::class.java,
            )
        }
        assertThrows(JsonParseException::class.java) {
            gson.fromJson(
                SUBMIT_RESPONSE.replace(
                    "\"submittedAt\":\"$SUBMITTED_AT\"",
                    "\"submittedAt\":null",
                ),
                GuestFeedbackSubmitResponseDto::class.java,
            )
        }
    }

    @Test
    fun `nullable 요청 값은 필수 JSON 키의 null로 직렬화한다`() {
        val json =
            gson
                .toJsonTree(
                    GuestFeedbackSubmitRequestDto(
                        nickname = null,
                        ratings =
                            listOf(
                                GuestFeedbackRatingDto(
                                    axis = GuestFeedbackAxisCodeDto.VOICE,
                                    level = 1,
                                    comment = null,
                                ),
                            ),
                    ),
                ).asJsonObject

        assertTrue(json.has("nickname"))
        assertTrue(json.get("nickname").isJsonNull)
        assertTrue(json.getAsJsonArray("ratings")[0].asJsonObject.has("comment"))
        assertTrue(
            json
                .getAsJsonArray("ratings")[0]
                .asJsonObject
                .get("comment")
                .isJsonNull,
        )
        assertEquals(
            "VOICE",
            JsonParser
                .parseString(json.toString())
                .asJsonObject
                .getAsJsonArray("ratings")[0]
                .asJsonObject
                .get("axis")
                .asString,
        )
    }

    private companion object {
        const val SUBMITTED_AT = "2026-07-30T09:58:13.348Z"
        const val OPEN_RESPONSE =
            """{
                "gate":"OPEN",
                "requesterName":"합성 요청자",
                "axes":[{"code":"GESTURE","displayName":"손동작"}],
                "videoUrl":"https://example.invalid/synthetic-video",
                "submissionOpen":true
            }"""
        const val CLOSED_RESPONSE =
            """{
                "gate":"FULL","requesterName":null,"axes":null,"videoUrl":null,
                "submissionOpen":null
            }"""
        const val MISSING_VIDEO_URL_RESPONSE =
            """{
                "gate":"FULL","requesterName":null,"axes":null,
                "submissionOpen":null
            }"""
        const val OPEN_NULL_RESPONSE =
            """{
                "gate":"OPEN","requesterName":"합성 요청자","axes":[],"videoUrl":null,
                "submissionOpen":true
            }"""
        const val CLOSED_WITH_DATA_RESPONSE =
            """{
                "gate":"FULL","requesterName":"합성 요청자","axes":null,"videoUrl":null,
                "submissionOpen":null
            }"""
        const val SUBMIT_RESPONSE =
            """{"submissionId":41,"submittedAt":"$SUBMITTED_AT"}"""
    }
}

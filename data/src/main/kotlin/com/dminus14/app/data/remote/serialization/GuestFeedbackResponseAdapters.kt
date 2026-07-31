package com.dminus14.app.data.remote.serialization

import com.dminus14.app.data.remote.dto.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.GuestFeedbackAxisDto
import com.dminus14.app.data.remote.dto.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackGateDto
import com.dminus14.app.data.remote.dto.GuestFeedbackQuestionBoundaryDto
import com.dminus14.app.data.remote.dto.GuestFeedbackSubmitResponseDto
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.Instant

/**
 * Guest Feedback 진입 JSON의 구조와 게이트별 null 계약을 검증한 뒤 유효한 DTO만 생성한다.
 *
 * 필수 키 누락, 잘못된 중첩 타입과 미지원 gate/axis를 DTO 생성 단계에서 거부해 별도 후검증기가
 * 필요하지 않게 한다. 오류에는 응답의 실제 민감 값을 포함하지 않는다.
 */
class GuestFeedbackEntryResponseAdapter : JsonDeserializer<GuestFeedbackEntryResponseDto> {
    /**
     * 원본 진입 JSON을 검증하고 `OPEN`이면 완전한 데이터, 그 외에는 명시적 null만 가진 DTO로
     * 변환한다.
     */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): GuestFeedbackEntryResponseDto {
        val response = json.requiredObject(RESPONSE_NAME)
        REQUIRED_KEYS.forEach(response::requiredElement)
        val gate =
            response
                .requiredElement(KEY_GATE)
                .strictEnum(KEY_GATE, GuestFeedbackGateDto.entries)

        return if (gate == GuestFeedbackGateDto.OPEN) {
            GuestFeedbackEntryResponseDto(
                gate = gate,
                requesterName = response.requiredString(KEY_REQUESTER_NAME),
                axes =
                    response.requiredArray(KEY_AXES).mapIndexed { index, element ->
                        element.toAxis(index)
                    },
                videoUrl = response.requiredString(KEY_VIDEO_URL),
                questionBoundaries =
                    response.requiredArray(KEY_QUESTION_BOUNDARIES).mapIndexed { index, element ->
                        element.toQuestionBoundary(index)
                    },
                submissionOpen = response.requiredBoolean(KEY_SUBMISSION_OPEN),
            )
        } else {
            OPEN_DATA_KEYS.forEach { key -> response.requireExplicitNull(key) }
            GuestFeedbackEntryResponseDto(
                gate = gate,
                requesterName = null,
                axes = null,
                videoUrl = null,
                questionBoundaries = null,
                submissionOpen = null,
            )
        }
    }

    /** axis 배열 원소의 필수 코드와 표시명을 검증해 non-null DTO로 변환한다. */
    private fun JsonElement.toAxis(index: Int): GuestFeedbackAxisDto {
        val axis = requiredObject("$KEY_AXES[$index]")
        return GuestFeedbackAxisDto(
            code =
                axis
                    .requiredElement(KEY_CODE)
                    .strictEnum(KEY_CODE, GuestFeedbackAxisCodeDto.entries),
            displayName = axis.requiredString(KEY_DISPLAY_NAME),
        )
    }

    /** 질문 배열 원소의 순번·시작 시각·원문을 검증해 non-null DTO로 변환한다. */
    private fun JsonElement.toQuestionBoundary(index: Int): GuestFeedbackQuestionBoundaryDto {
        val boundary = requiredObject("$KEY_QUESTION_BOUNDARIES[$index]")
        return GuestFeedbackQuestionBoundaryDto(
            turnLevel = boundary.requiredNumber(KEY_TURN_LEVEL, String::toIntOrNull),
            startAt = boundary.requiredNumber(KEY_START_AT, String::toDoubleOrNull),
            questionText = boundary.requiredString(KEY_QUESTION_TEXT),
        )
    }

    private companion object {
        const val RESPONSE_NAME = "Guest Feedback 진입 응답"
        const val KEY_GATE = "gate"
        const val KEY_REQUESTER_NAME = "requesterName"
        const val KEY_AXES = "axes"
        const val KEY_VIDEO_URL = "videoUrl"
        const val KEY_QUESTION_BOUNDARIES = "questionBoundaries"
        const val KEY_SUBMISSION_OPEN = "submissionOpen"
        const val KEY_CODE = "code"
        const val KEY_DISPLAY_NAME = "displayName"
        const val KEY_TURN_LEVEL = "turnLevel"
        const val KEY_START_AT = "startAt"
        const val KEY_QUESTION_TEXT = "questionText"

        val OPEN_DATA_KEYS =
            listOf(
                KEY_REQUESTER_NAME,
                KEY_AXES,
                KEY_VIDEO_URL,
                KEY_QUESTION_BOUNDARIES,
                KEY_SUBMISSION_OPEN,
            )
        val REQUIRED_KEYS = listOf(KEY_GATE) + OPEN_DATA_KEYS
    }
}

/**
 * Guest Feedback 제출 성공 JSON의 필수 ID와 UTC 제출 시각을 검증해 완전한 DTO를 생성한다.
 *
 * `submittedAt` 변환은 공용 [UtcInstantAdapter]에 위임해 모든 API의 UTC `Z` 규칙을 공유한다.
 */
class GuestFeedbackSubmitResponseAdapter : JsonDeserializer<GuestFeedbackSubmitResponseDto> {
    /** 필수 제출 결과를 읽고 누락 또는 null이면 DTO 생성 전에 파싱 오류로 중단한다. */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): GuestFeedbackSubmitResponseDto {
        val response = json.requiredObject(RESPONSE_NAME)
        val submittedAt: Instant? =
            context.deserialize(
                response.requiredNonNullElement(KEY_SUBMITTED_AT),
                Instant::class.java,
            )

        return GuestFeedbackSubmitResponseDto(
            submissionId = response.requiredNumber(KEY_SUBMISSION_ID, String::toLongOrNull),
            submittedAt = submittedAt ?: throw contractError(KEY_SUBMITTED_AT),
        )
    }

    private companion object {
        const val RESPONSE_NAME = "Guest Feedback 제출 응답"
        const val KEY_SUBMISSION_ID = "submissionId"
        const val KEY_SUBMITTED_AT = "submittedAt"
    }
}

/** JSON 값이 null이 아닌 객체인지 확인한다. */
private fun JsonElement.requiredObject(name: String): JsonObject {
    if (isJsonNull || !isJsonObject) throw contractError(name)
    return asJsonObject
}

/** 객체에 필수 키가 존재하는지 확인하되 명시적 null은 보존한다. */
private fun JsonObject.requiredElement(name: String): JsonElement {
    if (!has(name)) throw contractError(name)
    return get(name)
}

/** 객체의 필수 키가 존재하고 값도 null이 아닌지 확인한다. */
private fun JsonObject.requiredNonNullElement(name: String): JsonElement =
    requiredElement(name).takeUnless(JsonElement::isJsonNull) ?: throw contractError(name)

/** 필수 배열 값을 타입 변환 전에 검증한다. */
private fun JsonObject.requiredArray(name: String) =
    requiredNonNullElement(name).let { element ->
        if (!element.isJsonArray) throw contractError(name)
        element.asJsonArray
    }

/** 필수 문자열 값을 다른 기본 타입에서 강제 변환하지 않고 읽는다. */
private fun JsonObject.requiredString(name: String): String {
    val element = requiredNonNullElement(name)
    if (!element.isJsonPrimitive || !element.asJsonPrimitive.isString) throw contractError(name)
    return element.asString
}

/** 필수 Boolean 값을 문자열이나 숫자에서 강제 변환하지 않고 읽는다. */
private fun JsonObject.requiredBoolean(name: String): Boolean {
    val element = requiredNonNullElement(name)
    if (!element.isJsonPrimitive || !element.asJsonPrimitive.isBoolean) throw contractError(name)
    return element.asBoolean
}

/** 필수 숫자를 정확한 DTO 타입으로 변환하고 형식 오류를 응답 계약 오류로 통일한다. */
private fun <T> JsonObject.requiredNumber(
    name: String,
    convert: (String) -> T?,
): T {
    val element = requiredNonNullElement(name)
    if (!element.isJsonPrimitive || !element.asJsonPrimitive.isNumber) throw contractError(name)
    return convert(element.asString) ?: throw contractError(name)
}

/** 캐시된 `entries`에서 서버 이름과 정확히 일치하는 열거형 값만 찾아 배열 할당을 피한다. */
private fun <T : Enum<T>> JsonElement.strictEnum(
    name: String,
    entries: List<T>,
): T {
    if (isJsonNull || !isJsonPrimitive || !asJsonPrimitive.isString) throw contractError(name)
    return entries.firstOrNull { value -> value.name == asString }
        ?: throw contractError(name)
}

/** non-OPEN 응답의 필수 키가 생략되지 않고 명시적 null인지 확인한다. */
private fun JsonObject.requireExplicitNull(name: String) {
    if (!requiredElement(name).isJsonNull) throw contractError(name)
}

/** 민감한 실제 값 없이 위반 필드명만 포함하는 파싱 오류를 생성한다. */
private fun contractError(name: String): JsonParseException =
    JsonParseException("Guest Feedback 응답 계약을 위반한 필드가 있습니다: $name")

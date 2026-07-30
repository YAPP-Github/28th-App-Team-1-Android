package com.dminus14.app.data.remote.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * 서버 시각 문자열을 UTC를 명시한 [Instant]로만 역직렬화한다.
 *
 * 모든 API가 공유하는 시각 계약을 한곳에서 적용해 오프셋이 없거나 UTC가 아닌 값이
 * 데이터 계층 안으로 유입되는 것을 방지한다.
 */
class UtcInstantAdapter : JsonDeserializer<Instant> {
    /** UTC `Z` 형식의 JSON 문자열을 [Instant]로 변환한다. */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Instant {
        if (!json.isJsonPrimitive || !json.asJsonPrimitive.isString) {
            throw JsonParseException(INVALID_UTC_INSTANT_MESSAGE)
        }
        val value = json.asString

        if (!value.endsWith(UTC_SUFFIX)) {
            throw JsonParseException(INVALID_UTC_INSTANT_MESSAGE)
        }

        return try {
            Instant.parse(value)
        } catch (exception: DateTimeParseException) {
            throw JsonParseException(INVALID_UTC_INSTANT_MESSAGE, exception)
        }
    }

    private companion object {
        const val UTC_SUFFIX = "Z"
        const val INVALID_UTC_INSTANT_MESSAGE = "UTC Z 형식의 시각이 필요합니다."
    }
}

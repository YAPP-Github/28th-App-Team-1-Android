package com.dminus14.app.data.remote.dto.user

import com.dminus14.app.data.di.remote.network.GsonModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileUpdateDtoTest {
    private val gson = GsonModule.provideGson()

    @Test
    fun `이름 미변경 요청은 name 키를 JSON null로 직렬화한다`() {
        val request = UserProfileUpdateRequestDto(name = null, jobRole = "BACKEND", careerYears = 3)

        val json = gson.toJsonTree(request).asJsonObject

        assertTrue(json.has("name"))
        assertTrue(json.get("name").isJsonNull)
    }

    @Test
    fun `이름 변경 요청은 name 값을 문자열로 직렬화한다`() {
        val request =
            UserProfileUpdateRequestDto(
                name = "합성 사용자",
                jobRole = "BACKEND",
                careerYears = 3,
            )

        val json = gson.toJsonTree(request).asJsonObject

        assertEquals("합성 사용자", json.get("name").asString)
    }
}

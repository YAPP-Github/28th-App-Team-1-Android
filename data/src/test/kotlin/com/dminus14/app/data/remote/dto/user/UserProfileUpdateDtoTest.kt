package com.dminus14.app.data.remote.dto.user

import com.dminus14.app.data.di.remote.network.GsonModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileUpdateDtoTest {
    private val gson = GsonModule.provideGson()

    @Test
    fun `프로필 수정 요청은 name과 직무 및 연차 필드를 직렬화한다`() {
        val request =
            UserProfileUpdateRequestDto(
                name = "합성 사용자",
                jobRole = "BACKEND",
                careerYears = 3,
            )

        val json = gson.toJsonTree(request).asJsonObject

        assertEquals("합성 사용자", json.get("name").asString)
        assertEquals("BACKEND", json.get("jobRole").asString)
        assertEquals(3, json.get("careerYears").asInt)
    }
}

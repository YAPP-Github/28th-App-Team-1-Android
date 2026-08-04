package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {
    @Test
    fun `프로필 조회 응답의 모든 값을 도메인 모델로 변환한다`() {
        val response =
            UserProfileFetchResponseDto(
                userId = "synthetic-user-id",
                name = "합성 사용자",
                email = "synthetic@example.invalid",
                provider = "KAKAO",
                jobRole = "BACKEND",
                jobRoleLabel = "백엔드",
                careerYears = 3,
                remainingTicketCount = 2,
            )

        val actual = response.toDomain()

        assertEquals(
            UserProfile(
                name = "합성 사용자",
                email = "synthetic@example.invalid",
                provider = "KAKAO",
                jobRole = "BACKEND",
                jobRoleLabel = "백엔드",
                careerYears = 3,
                remainingTicketCount = 2,
            ),
            actual,
        )
    }

    @Test
    fun `프로필 조회 응답의 null을 기본값으로 바꾸지 않는다`() {
        val actual = UserProfileFetchResponseDto(name = "합성 사용자").toDomain()

        assertEquals(
            UserProfile(
                name = "합성 사용자",
                email = null,
                provider = null,
                jobRole = null,
                jobRoleLabel = null,
                careerYears = null,
                remainingTicketCount = null,
            ),
            actual,
        )
    }

    @Test
    fun `프로필 수정 도메인 입력을 요청 DTO로 변환한다`() {
        val update = UserProfileUpdate(name = "합성 사용자", jobRole = "BACKEND", careerYears = 3)

        val actual = update.toDto()

        assertEquals("합성 사용자", actual.name)
        assertEquals("BACKEND", actual.jobRole)
        assertEquals(3, actual.careerYears)
    }
}

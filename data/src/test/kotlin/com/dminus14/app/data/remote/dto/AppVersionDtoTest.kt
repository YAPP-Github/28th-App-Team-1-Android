package com.dminus14.app.data.remote.dto

import com.dminus14.app.data.remote.dto.appversion.AppVersionCheckResponseDto
import com.dminus14.app.domain.model.AppVersionUpdateType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppVersionDtoTest {
    @Test
    fun `FORCE 응답을 도메인 모델로 변환한다`() {
        val actual =
            AppVersionCheckResponseDto(
                updateType = "FORCE",
                latestVersion = "1.4.0",
                minSupportedVersion = "1.3.0",
                storeUrl = "https://apps.apple.com/app/idXXXXXXXXX",
                title = "업데이트가 필요해요",
                body = "지금 버전에서는 앱을 이용할 수 없어요. 최신 버전으로 업데이트해 주세요.",
            ).toDomain()

        assertEquals(AppVersionUpdateType.FORCE, actual.updateType)
        assertEquals("1.4.0", actual.latestVersion)
        assertEquals("1.3.0", actual.minSupportedVersion)
        assertEquals("업데이트가 필요해요", actual.title)
    }

    @Test
    fun `OPTIONAL 응답을 도메인 모델로 변환한다`() {
        val actual =
            AppVersionCheckResponseDto(
                updateType = "OPTIONAL",
                latestVersion = "1.4.0",
                minSupportedVersion = "1.3.0",
                storeUrl = "https://apps.apple.com/app/idXXXXXXXXX",
                title = "새 버전이 나왔어요",
                body = "면접 연습 화면이 더 빨라졌어요. 지금 업데이트할까요?",
            ).toDomain()

        assertEquals(AppVersionUpdateType.OPTIONAL, actual.updateType)
        assertEquals("새 버전이 나왔어요", actual.title)
    }

    @Test
    fun `NONE 응답은 서버에 title body가 있어도 도메인에서는 null로 정규화한다`() {
        val actual =
            AppVersionCheckResponseDto(
                updateType = "NONE",
                latestVersion = "1.4.0",
                minSupportedVersion = "1.3.0",
                storeUrl = "https://apps.apple.com/app/idXXXXXXXXX",
                title = "무시되어야 할 제목",
                body = "무시되어야 할 본문",
            ).toDomain()

        assertEquals(AppVersionUpdateType.NONE, actual.updateType)
        assertNull(actual.title)
        assertNull(actual.body)
    }

    @Test
    fun `알 수 없는 updateType 문자열은 UNKNOWN으로 흡수한다`() {
        val actual =
            AppVersionCheckResponseDto(
                updateType = "FUTURE_TYPE",
                latestVersion = "1.4.0",
                minSupportedVersion = "1.3.0",
                storeUrl = "https://apps.apple.com/app/idXXXXXXXXX",
                title = null,
                body = null,
            ).toDomain()

        assertEquals(AppVersionUpdateType.UNKNOWN, actual.updateType)
    }
}

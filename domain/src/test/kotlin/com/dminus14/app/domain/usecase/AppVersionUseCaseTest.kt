package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.AppVersionPolicy
import com.dminus14.app.domain.model.AppVersionUpdateType
import com.dminus14.app.domain.repository.AppVersionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AppVersionUseCaseTest {
    @Test
    fun `버전 확인은 저장소를 한 번 호출하고 결과를 그대로 전달한다`() =
        runTest {
            val repository = FakeAppVersionRepository()
            val useCase = CheckAppVersionUseCase(repository)

            val result = useCase(version = "1.2.0")

            assertSame(repository.policy, result.getOrThrow())
            assertEquals(1, repository.callCount)
            assertEquals("1.2.0", repository.requestedVersion)
        }

    private class FakeAppVersionRepository(
        val policy: AppVersionPolicy =
            AppVersionPolicy(
                updateType = AppVersionUpdateType.NONE,
                latestVersion = "1.4.0",
                minSupportedVersion = "1.3.0",
                storeUrl = "https://play.google.com/store/apps/details?id=com.dminus14.app",
                title = null,
                body = null,
            ),
    ) : AppVersionRepository {
        var callCount = 0
            private set
        var requestedVersion: String? = null
            private set

        override suspend fun checkAppVersion(version: String): AppVersionPolicy {
            callCount += 1
            requestedVersion = version
            return policy
        }
    }
}

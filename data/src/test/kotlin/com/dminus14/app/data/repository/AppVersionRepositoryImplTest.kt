package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.AppVersionRemoteDataSource
import com.dminus14.app.data.remote.dto.AppVersionCheckResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.InvalidPlatformException
import com.dminus14.app.domain.exception.InvalidVersionFormatException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.AppVersionUpdateType
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AppVersionRepositoryImplTest {
    @Test
    fun `버전 확인을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeAppVersionRemoteDataSource()
        val repository = AppVersionRepositoryImpl(dataSource)

        val actual = runBlocking { repository.checkAppVersion("1.2.0") }

        assertEquals(AppVersionUpdateType.OPTIONAL, actual.updateType)
        assertEquals("ANDROID", dataSource.requestedPlatform)
        assertEquals("1.2.0", dataSource.requestedVersion)
    }

    @Test
    fun `비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.INVALID_PLATFORM to InvalidPlatformException::class.java,
                ApiErrorCode.INVALID_VERSION_FORMAT to InvalidVersionFormatException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(400, code)
            val repository =
                AppVersionRepositoryImpl(
                    FakeAppVersionRemoteDataSource(failure = httpError),
                )

            val actual = captureFailure { repository.checkAppVersion("1.2.0") }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `버전 정책 미존재 오류는 ServerException으로 격상한다`() {
        val httpError = httpException(404, ApiErrorCode.APP_VERSION_POLICY_NOT_FOUND)
        val repository =
            AppVersionRepositoryImpl(
                FakeAppVersionRemoteDataSource(failure = httpError),
            )

        val actual = captureFailure { repository.checkAppVersion("1.2.0") }

        assertTrue(actual is ServerException)
        assertEquals(ApiErrorCode.APP_VERSION_POLICY_NOT_FOUND, (actual as CustomException).errCode)
        assertSame(httpError, actual.cause)
    }

    @Test
    fun `공통 네트워크 서버 알 수 없는 오류 정책을 유지한다`() {
        val cases =
            listOf(
                IOException("synthetic offline") to NetworkUnavailableException::class.java,
                httpException(500, "SYNTHETIC_SERVER_ERROR") to ServerException::class.java,
                IllegalStateException("synthetic invalid state") to UnknownException::class.java,
            )

        cases.forEach { (failure, expectedType) ->
            val repository =
                AppVersionRepositoryImpl(
                    FakeAppVersionRemoteDataSource(failure = failure),
                )

            val actual = captureFailure { repository.checkAppVersion("1.2.0") }

            assertTrue(expectedType.isInstance(actual))
            assertSame(failure, actual.cause)
        }
    }

    private fun captureFailure(block: suspend () -> Unit): Throwable {
        try {
            runBlocking { block() }
        } catch (error: Throwable) {
            return error
        }
        throw AssertionError("예외가 발생해야 합니다.")
    }

    private fun httpException(
        status: Int,
        code: String,
    ): HttpException {
        val body =
            """
            {"success":false,"code":"$code","message":"synthetic $code"}
            """.trimIndent()
                .toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(status, body))
    }

    private class FakeAppVersionRemoteDataSource(
        private val failure: Throwable? = null,
        private val response: AppVersionCheckResponseDto =
            AppVersionCheckResponseDto(
                updateType = "OPTIONAL",
                latestVersion = "1.4.0",
                minSupportedVersion = "1.3.0",
                storeUrl = "https://play.google.com/store/apps/details?id=com.dminus14.app",
                title = "새 버전이 나왔어요",
                body = "지금 업데이트할까요?",
            ),
    ) : AppVersionRemoteDataSource {
        var requestedPlatform: String? = null
            private set
        var requestedVersion: String? = null
            private set

        override suspend fun checkAppVersion(
            platform: String,
            version: String,
        ): AppVersionCheckResponseDto {
            failure?.let { throw it }
            requestedPlatform = platform
            requestedVersion = version
            return response
        }
    }
}

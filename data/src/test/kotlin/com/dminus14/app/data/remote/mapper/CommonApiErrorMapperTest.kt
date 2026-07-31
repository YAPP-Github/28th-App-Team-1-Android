package com.dminus14.app.data.remote.mapper

import com.dminus14.app.domain.exception.InvalidCredentialException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CommonApiErrorMapperTest {
    @Test
    fun `CancellationException은 domain 예외로 변환하지 않고 그대로 다시 던진다`() {
        val cause = CancellationException("cancelled")

        val thrown =
            assertThrows(CancellationException::class.java) { CommonApiErrorMapper.map(cause) }

        assertSame(cause, thrown)
    }

    @Test
    fun `IOException은 NetworkUnavailableException으로 변환한다`() {
        val cause = IOException("offline")

        val mapped = CommonApiErrorMapper.map(cause)

        assertTrue(mapped is NetworkUnavailableException)
        assertEquals(
            ApiErrorCode.NETWORK_UNAVAILABLE,
            (mapped as NetworkUnavailableException).errCode,
        )
        assertSame(cause, mapped.cause)
    }

    @Test
    fun `HTTP 500은 ServerException으로 변환한다`() {
        val mapped = CommonApiErrorMapper.map(httpException(500))

        assertTrue(mapped is ServerException)
        assertEquals(ApiErrorCode.SERVER_ERROR, (mapped as ServerException).errCode)
    }

    @Test
    fun `HTTP 500 바디 code가 있으면 ServerException errCode로 사용한다`() {
        val mapped =
            CommonApiErrorMapper.map(
                httpException(
                    500,
                    """{"success":false,"code":"UPSTREAM_TIMEOUT","message":"timeout"}""",
                ),
            )

        assertTrue(mapped is ServerException)
        assertEquals("UPSTREAM_TIMEOUT", (mapped as ServerException).errCode)
    }

    @Test
    fun `인식하지 못한 HTTP 오류는 UnknownException으로 변환한다`() {
        val mapped =
            CommonApiErrorMapper.map(
                httpException(
                    400,
                    """{"success":false,"code":"SOMETHING_ELSE","message":"bad"}""",
                ),
            )

        assertTrue(mapped is UnknownException)
        assertEquals("SOMETHING_ELSE", (mapped as UnknownException).errCode)
        assertEquals("bad", mapped.message)
    }

    @Test
    fun `mapBusiness가 반환한 예외는 공통 변환보다 우선한다`() {
        val mapped =
            CommonApiErrorMapper.map(
                httpException(
                    400,
                    """{"success":false,"code":"INVALID_CREDENTIAL","message":"invalid"}""",
                ),
            ) { http, apiError ->
                InvalidCredentialException(
                    errCode = checkNotNull(apiError).code,
                    message = apiError.message,
                    cause = http,
                )
            }

        assertTrue(mapped is InvalidCredentialException)
        assertEquals(
            ApiErrorCode.INVALID_CREDENTIAL,
            (mapped as InvalidCredentialException).errCode,
        )
        assertEquals("invalid", mapped.message)
    }

    @Test
    fun `IllegalStateException은 UnknownException으로 변환한다`() {
        val cause = IllegalStateException("broken response")

        val mapped = CommonApiErrorMapper.map(cause)

        assertTrue(mapped is UnknownException)
        assertEquals(ApiErrorCode.UNKNOWN, (mapped as UnknownException).errCode)
        assertEquals("broken response", mapped.message)
        assertSame(cause, mapped.cause)
    }

    @Test
    fun `그 외 Throwable은 원본을 유지한다`() {
        val cause = RuntimeException("unexpected")

        val mapped = CommonApiErrorMapper.map(cause)

        assertSame(cause, mapped)
    }

    private fun httpException(
        code: Int,
        body: String = "",
    ): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(code, responseBody))
    }
}

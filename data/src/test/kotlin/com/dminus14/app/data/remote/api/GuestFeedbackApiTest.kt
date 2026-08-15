package com.dminus14.app.data.remote.api

import com.dminus14.app.data.di.remote.network.GsonModule
import com.dminus14.app.data.di.remote.network.NetworkModule
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitRequestDto
import com.dminus14.app.data.remote.installation.FakeInstallationIdStore
import com.dminus14.app.data.remote.installation.InstallationIdProvider
import com.dminus14.app.data.remote.interceptor.InsertInstallationIdInterceptor
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GuestFeedbackApiTest {
    @Test
    fun `진입 요청은 토큰을 인코딩하고 Device-Id만 한 번 전송한다`() {
        MockWebServer().use { server ->
            server.enqueue(jsonResponse(OPEN_RESPONSE))
            val api = createApi(server)

            runBlocking { api.enter(SYNTHETIC_TOKEN) }

            val request = server.takeRequest()
            assertEquals(
                "/api/v1/feedback/guest/synthetic%20token%2F1",
                request.path,
            )
            assertEquals("GET", request.method)
            assertEquals(listOf(DEVICE_ID), request.headers.values(DEVICE_ID_HEADER))
            assertNull(request.getHeader(INSTALLATION_ID_MARKER))
            assertNull(request.getHeader(AUTHORIZATION_HEADER))
        }
    }

    @Test
    fun `제출 요청은 nullable 필수 키를 null로 전송하고 직접 응답을 반환한다`() {
        MockWebServer().use { server ->
            server.enqueue(jsonResponse(SUBMIT_RESPONSE).setResponseCode(201))
            val api = createApi(server)

            val response =
                runBlocking {
                    api.submit(
                        token = SYNTHETIC_TOKEN,
                        request =
                            GuestFeedbackSubmitRequestDto(
                                nickname = null,
                                ratings =
                                    listOf(
                                        GuestFeedbackRatingDto(
                                            axis = GuestFeedbackAxisCodeDto.GAZE,
                                            level = 2,
                                            comment = null,
                                        ),
                                    ),
                            ),
                    )
                }

            val request = server.takeRequest()
            val body = JsonParser.parseString(request.body.readUtf8()).asJsonObject
            assertEquals("POST", request.method)
            assertEquals("application/json; charset=UTF-8", request.getHeader("Content-Type"))
            assertTrue(body.has("nickname"))
            assertTrue(body.get("nickname").isJsonNull)
            assertTrue(body.getAsJsonArray("ratings")[0].asJsonObject.has("comment"))
            assertTrue(
                body
                    .getAsJsonArray("ratings")[0]
                    .asJsonObject
                    .get("comment")
                    .isJsonNull,
            )
            assertEquals(41L, response.submissionId)
            assertEquals(listOf(DEVICE_ID), request.headers.values(DEVICE_ID_HEADER))
            assertNull(request.getHeader(AUTHORIZATION_HEADER))
        }
    }

    @Test
    fun `Guest 클라이언트에는 인증 로거와 디스크 캐시가 없다`() {
        val interceptor =
            InsertInstallationIdInterceptor(
                InstallationIdProvider(FakeInstallationIdStore(initialValue = DEVICE_ID)),
            )

        val client = NetworkModule.provideGuestOkHttpClient(interceptor)

        assertEquals(listOf(interceptor), client.interceptors)
        assertSame(Authenticator.NONE, client.authenticator)
        assertNull(client.cache)
        assertFalse(client.interceptors.any { it.javaClass.simpleName.contains("Logging") })
    }

    /** 실제 서버 호출 없이 Guest 전용 Gson과 설치 ID interceptor가 연결된 API를 구성한다. */
    private fun createApi(server: MockWebServer): GuestFeedbackApi {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(
                    InsertInstallationIdInterceptor(
                        InstallationIdProvider(
                            FakeInstallationIdStore(initialValue = DEVICE_ID),
                        ),
                    ),
                ).build()
        return Retrofit
            .Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonModule.provideGuestGson(GsonModule.provideGson()),
                ),
            ).build()
            .create(GuestFeedbackApi::class.java)
    }

    private fun jsonResponse(body: String): MockResponse =
        MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(body)

    private companion object {
        const val SYNTHETIC_TOKEN = "synthetic token/1"
        const val DEVICE_ID = "123e4567-e89b-12d3-a456-426614174000"
        const val DEVICE_ID_HEADER = "Device-Id"
        const val INSTALLATION_ID_MARKER = "X-Installation-Id-Required"
        const val AUTHORIZATION_HEADER = "Authorization"
        const val OPEN_RESPONSE =
            """{
                "gate":"OPEN",
                "requesterName":"합성 요청자",
                "axes":[{"code":"GAZE","displayName":"시선"}],
                "videoUrl":"https://example.invalid/synthetic-video",
                "questionBoundaries":[{"turnLevel":1,"startAt":2.5,"questionText":"합성 질문"}],
                "submissionOpen":true
            }"""
        const val SUBMIT_RESPONSE =
            """{"submissionId":41,"submittedAt":"2026-07-30T09:58:13.348Z"}"""
    }
}

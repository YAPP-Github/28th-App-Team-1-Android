package com.dminus14.app.data.remote.interceptor

import com.dminus14.app.data.remote.installation.FakeInstallationIdStore
import com.dminus14.app.data.remote.installation.InstallationIdProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class InsertInstallationIdInterceptorTest {
    @Test
    fun `선택 표식이 없으면 Provider를 호출하지 않는다`() {
        val store = FakeInstallationIdStore(initialValue = INSTALLATION_ID)
        val client = createClient(store)

        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(204))

            client
                .newCall(Request.Builder().url(server.url("/without-marker")).build())
                .execute()
                .use { response -> assertEquals(204, response.code) }

            val recordedRequest = server.takeRequest()
            assertNull(recordedRequest.getHeader(InsertInstallationIdInterceptor.HEADER_DEVICE_ID))
            assertEquals(0, store.getCount)
        }
    }

    @Test
    fun `선택 표식을 제거하고 Device-Id를 한 개 추가한다`() {
        val store = FakeInstallationIdStore(initialValue = INSTALLATION_ID)
        val client = createClient(store)

        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(204))
            val request =
                Request
                    .Builder()
                    .url(server.url("/with-marker"))
                    .header(
                        InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED,
                        InsertInstallationIdInterceptor.INSTALLATION_ID_REQUIRED_VALUE,
                    ).build()

            client.newCall(request).execute().use { response ->
                assertEquals(204, response.code)
            }

            val recordedRequest = server.takeRequest()
            assertNull(
                recordedRequest.getHeader(
                    InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED,
                ),
            )
            assertEquals(
                listOf(INSTALLATION_ID),
                recordedRequest.headers.values(InsertInstallationIdInterceptor.HEADER_DEVICE_ID),
            )
        }
    }

    @Test
    fun `기존 Device-Id를 Provider 값으로 덮어쓴다`() {
        val client = createClient(FakeInstallationIdStore(initialValue = INSTALLATION_ID))

        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(204))
            val request =
                Request
                    .Builder()
                    .url(server.url("/replace-header"))
                    .header(
                        InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED,
                        InsertInstallationIdInterceptor.INSTALLATION_ID_REQUIRED_VALUE,
                    ).header(
                        InsertInstallationIdInterceptor.HEADER_DEVICE_ID,
                        OLD_INSTALLATION_ID,
                    ).build()

            client.newCall(request).execute().close()

            assertEquals(
                listOf(INSTALLATION_ID),
                server.takeRequest().headers.values(
                    InsertInstallationIdInterceptor.HEADER_DEVICE_ID,
                ),
            )
        }
    }

    @Test
    fun `Provider 실패 시 서버로 요청을 보내지 않는다`() {
        val store = FakeInstallationIdStore(getFailure = IOException("synthetic failure"))
        val client = createClient(store)

        MockWebServer().use { server ->
            val request =
                Request
                    .Builder()
                    .url(server.url("/provider-failure"))
                    .header(
                        InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED,
                        InsertInstallationIdInterceptor.INSTALLATION_ID_REQUIRED_VALUE,
                    ).build()

            assertThrows(IOException::class.java) {
                client.newCall(request).execute()
            }
            assertEquals(0, server.requestCount)
        }
    }

    private fun createClient(store: FakeInstallationIdStore): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(InsertInstallationIdInterceptor(InstallationIdProvider(store)))
            .build()

    private companion object {
        const val INSTALLATION_ID = "123e4567-e89b-12d3-a456-426614174000"
        const val OLD_INSTALLATION_ID = "00000000-0000-0000-0000-000000000000"
    }
}

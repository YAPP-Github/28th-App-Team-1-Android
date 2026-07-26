package com.dminus14.app.data.remote.interceptor

import com.dminus14.app.data.remote.installation.FakeInstallationIdStore
import com.dminus14.app.data.remote.installation.InstallationIdProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class OkHttpLoggingInterceptorFactoryTest {
    @Test
    fun `기본 HTTP 로그에서 인증 토큰과 Device-Id 원문을 가린다`() {
        verifySensitiveHeadersAreRedacted(OkHttpLoggingInterceptorFactory::create)
    }

    @Test
    fun `업로드 HTTP 로그에서 인증 토큰과 Device-Id 원문을 가린다`() {
        verifySensitiveHeadersAreRedacted(OkHttpLoggingInterceptorFactory::createForUpload)
    }

    @Test
    fun `내부 선택 표식은 서버 요청과 HTTP 로그에 남지 않는다`() {
        val messages = mutableListOf<String>()
        val logger = HttpLoggingInterceptor.Logger { message -> messages += message }
        val installationIdProvider =
            InstallationIdProvider(FakeInstallationIdStore(initialValue = INSTALLATION_ID))
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(InsertInstallationIdInterceptor(installationIdProvider))
                .addInterceptor(OkHttpLoggingInterceptorFactory.create(logger))
                .build()

        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(204))
            val request =
                Request
                    .Builder()
                    .url(server.url("/marker-redaction"))
                    .header(
                        InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED,
                        InsertInstallationIdInterceptor.INSTALLATION_ID_REQUIRED_VALUE,
                    ).build()

            client.newCall(request).execute().close()

            assertNull(
                server
                    .takeRequest()
                    .getHeader(InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED),
            )
            assertFalse(
                messages
                    .joinToString("\n")
                    .contains(InsertInstallationIdInterceptor.HEADER_INSTALLATION_ID_REQUIRED),
            )
        }
    }

    private fun verifySensitiveHeadersAreRedacted(
        factory: (HttpLoggingInterceptor.Logger) -> okhttp3.Interceptor,
    ) {
        val messages = mutableListOf<String>()
        val logger = HttpLoggingInterceptor.Logger { message -> messages += message }
        val client = OkHttpClient.Builder().addInterceptor(factory(logger)).build()

        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(204))
            val request =
                Request
                    .Builder()
                    .url(server.url("/redaction"))
                    .header("Authorization", AUTHORIZATION_VALUE)
                    .header(InsertInstallationIdInterceptor.HEADER_DEVICE_ID, INSTALLATION_ID)
                    .build()

            client.newCall(request).execute().close()

            val joinedMessages = messages.joinToString("\n")
            assertFalse(joinedMessages.contains(AUTHORIZATION_VALUE))
            assertFalse(joinedMessages.contains(INSTALLATION_ID))
        }
    }

    private companion object {
        const val AUTHORIZATION_VALUE = "Bearer synthetic-access-token"
        const val INSTALLATION_ID = "123e4567-e89b-12d3-a456-426614174000"
    }
}

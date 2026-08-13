package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.repository.DynamicLinkRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateFeedbackShareDynamicLinkUseCaseTest {
    @Test
    fun `동적 링크 생성이 성공하면 그 링크를 반환한다`() {
        val repository =
            FakeDynamicLinkRepository(linkResult = Result.success("https://short.link/abc"))
        val useCase = CreateFeedbackShareDynamicLinkUseCase(repository)

        val actual = runBlocking { useCase("token123") }

        assertTrue(actual.isSuccess)
        assertEquals("https://short.link/abc", actual.getOrNull())
        assertEquals("hilit://feedback/token123", repository.requestedDeepLink)
    }

    @Test
    fun `동적 링크 생성이 실패하면 원시 딥링크로 대체한다`() {
        val repository =
            FakeDynamicLinkRepository(linkResult = Result.failure(IllegalStateException("서버 오류")))
        val useCase = CreateFeedbackShareDynamicLinkUseCase(repository)

        val actual = runBlocking { useCase("token123") }

        assertTrue(actual.isSuccess)
        assertEquals("hilit://feedback/token123", actual.getOrNull())
    }

    private class FakeDynamicLinkRepository(
        private val linkResult: Result<String>,
    ) : DynamicLinkRepository {
        var requestedDeepLink: String? = null
            private set

        override suspend fun createLink(deepLink: String): String {
            requestedDeepLink = deepLink
            return linkResult.getOrThrow()
        }
    }
}

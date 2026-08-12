package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.FeedbackShareValidationException
import com.dminus14.app.domain.model.FeedbackShare
import com.dminus14.app.domain.model.FeedbackShareStatus
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FeedbackShareUseCaseTest {
    @Test
    fun `상태 조회는 저장소를 한 번 호출하고 결과를 그대로 반환한다`() =
        runTest {
            val repository = FakeFeedbackShareRepository()

            val result = GetFeedbackShareStatusUseCase(repository)(SESSION_ID)

            assertSame(repository.status, result.getOrThrow())
            assertEquals(SESSION_ID, repository.statusSessionId)
            assertEquals(1, repository.statusCallCount)
        }

    @Test
    fun `한 개에서 다섯 개 사이의 중복 없는 항목이면 저장소를 호출해 토큰을 반환한다`() =
        runTest {
            val repository = FakeFeedbackShareRepository()

            val result = CreateFeedbackShareUseCase(repository)(SESSION_ID, validAxes())

            assertEquals(repository.createdToken, result.getOrThrow())
            assertEquals(SESSION_ID, repository.createSessionId)
            assertEquals(validAxes(), repository.createdAxes)
        }

    @Test
    fun `지정 항목이 비어 있거나 다섯 개를 초과하면 저장소를 호출하지 않는다`() =
        runTest {
            val tooManyAxes = GuestFeedbackAxisCode.entries + GuestFeedbackAxisCode.GAZE

            listOf(emptyList(), tooManyAxes).forEach { axes ->
                val repository = FakeFeedbackShareRepository()

                val result = CreateFeedbackShareUseCase(repository)(SESSION_ID, axes)

                assertTrue(result.exceptionOrNull() is FeedbackShareValidationException)
                assertEquals(0, repository.createCallCount)
            }
        }

    @Test
    fun `지정 항목이 중복되면 저장소를 호출하지 않는다`() =
        runTest {
            val repository = FakeFeedbackShareRepository()
            val duplicateAxes = listOf(GuestFeedbackAxisCode.GAZE, GuestFeedbackAxisCode.GAZE)

            val result = CreateFeedbackShareUseCase(repository)(SESSION_ID, duplicateAxes)

            assertTrue(result.exceptionOrNull() is FeedbackShareValidationException)
            assertEquals(0, repository.createCallCount)
        }

    @Test
    fun `비공개 전환은 저장소를 한 번 호출한다`() =
        runTest {
            val repository = FakeFeedbackShareRepository()

            val result = MakeFeedbackSharePrivateUseCase(repository)(SESSION_ID)

            assertTrue(result.isSuccess)
            assertEquals(SESSION_ID, repository.makePrivateSessionId)
            assertEquals(1, repository.makePrivateCallCount)
        }

    @Test
    fun `저장소 오류는 실패 결과로 전달한다`() =
        runTest {
            val failure = IllegalStateException("synthetic failure")
            val repository = FakeFeedbackShareRepository(failure = failure)

            val result = GetFeedbackShareStatusUseCase(repository)(SESSION_ID)

            assertSame(failure, result.exceptionOrNull())
        }

    @Test
    fun `저장소 취소 예외는 실패 결과로 감싸지 않는다`() =
        runTest {
            val repository =
                FakeFeedbackShareRepository(
                    failure = CancellationException("synthetic cancellation"),
                )

            try {
                GetFeedbackShareStatusUseCase(repository)(SESSION_ID)
                fail("CancellationException이 다시 던져져야 합니다.")
            } catch (_: CancellationException) {
                Unit
            }
        }

    private class FakeFeedbackShareRepository(
        val status: FeedbackShare = defaultStatus(),
        val createdToken: String = "synthetic-created-token",
        private val failure: Throwable? = null,
    ) : FeedbackShareRepository {
        var statusCallCount = 0
            private set
        var statusSessionId: Long? = null
            private set
        var createCallCount = 0
            private set
        var createSessionId: Long? = null
            private set
        var createdAxes: List<GuestFeedbackAxisCode>? = null
            private set
        var makePrivateCallCount = 0
            private set
        var makePrivateSessionId: Long? = null
            private set

        override suspend fun getStatus(sessionId: Long): FeedbackShare {
            failure?.let { throw it }
            statusCallCount += 1
            statusSessionId = sessionId
            return status
        }

        override suspend fun create(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): String {
            failure?.let { throw it }
            createCallCount += 1
            createSessionId = sessionId
            createdAxes = axes
            return createdToken
        }

        override suspend fun makePrivate(sessionId: Long) {
            failure?.let { throw it }
            makePrivateCallCount += 1
            makePrivateSessionId = sessionId
        }
    }

    private companion object {
        const val SESSION_ID = 41L

        fun validAxes(): List<GuestFeedbackAxisCode> =
            listOf(GuestFeedbackAxisCode.GAZE, GuestFeedbackAxisCode.VOICE)

        fun defaultStatus(): FeedbackShare =
            FeedbackShare(
                token = "synthetic-token",
                status = FeedbackShareStatus.ACTIVE,
                axes = validAxes(),
                submittedCount = 2,
                videoExpiresAt = null,
                requestedAt = null,
            )
    }
}

package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.GuestFeedbackValidationException
import com.dminus14.app.domain.model.GuestFeedbackAxis
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackRating
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.model.GuestFeedbackUnavailableReason
import com.dminus14.app.domain.repository.GuestFeedbackRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class GuestFeedbackUseCaseTest {
    @Test
    fun `진입 시 토큰 양끝 공백을 제거하고 저장소를 한 번 호출한다`() =
        runTest {
            val repository = FakeGuestFeedbackRepository()
            val useCase = EnterGuestFeedbackUseCase(repository)

            val result = useCase("  synthetic-token\n")

            assertSame(repository.entry, result.getOrThrow())
            assertEquals("synthetic-token", repository.enteredToken)
            assertEquals(1, repository.enterCallCount)
        }

    @Test
    fun `빈 토큰이면 진입과 제출 저장소를 호출하지 않는다`() =
        runTest {
            val repository = FakeGuestFeedbackRepository()

            val enterResult = EnterGuestFeedbackUseCase(repository)(" \n\t")
            val submitResult =
                SubmitGuestFeedbackUseCase(repository)(
                    token = " \n\t",
                    axes = validAxes(),
                    submission = validSubmission(),
                )

            assertTrue(enterResult.exceptionOrNull() is GuestFeedbackValidationException)
            assertTrue(submitResult.exceptionOrNull() is GuestFeedbackValidationException)
            assertEquals(0, repository.enterCallCount)
            assertEquals(0, repository.submitCallCount)
        }

    @Test
    fun `작성 불가 게이트도 정상 진입 결과로 반환한다`() =
        runTest {
            GuestFeedbackUnavailableReason.entries.forEach { reason ->
                val repository =
                    FakeGuestFeedbackRepository(
                        entry = GuestFeedbackEntry.Unavailable(reason),
                    )

                val result = EnterGuestFeedbackUseCase(repository)(SYNTHETIC_TOKEN)

                assertEquals(GuestFeedbackEntry.Unavailable(reason), result.getOrThrow())
            }
        }

    @Test
    fun `지정된 항목을 정확히 한 번씩 평가하면 정규화해 제출한다`() =
        runTest {
            val repository = FakeGuestFeedbackRepository()
            val submission =
                GuestFeedbackSubmission(
                    nickname = "  합성 지인  ",
                    ratings =
                        listOf(
                            GuestFeedbackRating(
                                axis = GuestFeedbackAxisCode.GAZE,
                                level = 1,
                                comment = "  좋아요 😀\n  ",
                            ),
                            GuestFeedbackRating(
                                axis = GuestFeedbackAxisCode.VOICE,
                                level = 4,
                                comment = null,
                            ),
                        ),
                )

            val result =
                SubmitGuestFeedbackUseCase(repository)(
                    token = " $SYNTHETIC_TOKEN ",
                    axes = validAxes(),
                    submission = submission,
                )

            assertTrue(result.isSuccess)
            assertEquals(SYNTHETIC_TOKEN, repository.submittedToken)
            assertEquals("합성 지인", repository.submittedSubmission?.nickname)
            assertEquals(
                "좋아요 😀",
                repository.submittedSubmission
                    ?.ratings
                    ?.first()
                    ?.comment,
            )
            assertEquals(
                "",
                repository.submittedSubmission
                    ?.ratings
                    ?.last()
                    ?.comment,
            )
            assertEquals(1, repository.submitCallCount)
        }

    @Test
    fun `별칭이 없거나 공백뿐이면 익명의 지인으로 제출한다`() =
        runTest {
            listOf<String?>(null, "", " \n\t").forEach { nickname ->
                val repository = FakeGuestFeedbackRepository()
                val submission = validSubmission().copy(nickname = nickname)

                val result =
                    SubmitGuestFeedbackUseCase(repository)(
                        token = SYNTHETIC_TOKEN,
                        axes = validAxes(),
                        submission = submission,
                    )

                assertTrue(result.isSuccess)
                assertEquals("익명의 지인", repository.submittedSubmission?.nickname)
            }
        }

    @Test
    fun `지정 항목 수가 범위를 벗어나면 제출하지 않는다`() =
        runTest {
            val tooManyAxes = validAxes() + validAxes() + validAxes().first()

            listOf(emptyList(), tooManyAxes).forEach { axes ->
                assertRejected(axes = axes, submission = validSubmission())
            }
        }

    @Test
    fun `지정 항목이나 제출 항목이 중복되면 제출하지 않는다`() =
        runTest {
            val duplicateAxes = validAxes() + validAxes().first()
            val duplicateRatings =
                validSubmission().copy(
                    ratings =
                        listOf(
                            validSubmission().ratings.first(),
                            validSubmission().ratings.first(),
                        ),
                )

            assertRejected(axes = duplicateAxes, submission = validSubmission())
            assertRejected(axes = validAxes(), submission = duplicateRatings)
        }

    @Test
    fun `지정 항목이 누락되거나 추가되면 제출하지 않는다`() =
        runTest {
            val missingRating =
                validSubmission().copy(ratings = validSubmission().ratings.dropLast(1))
            val unexpectedRating =
                validSubmission().copy(
                    ratings =
                        validSubmission().ratings +
                            GuestFeedbackRating(
                                axis = GuestFeedbackAxisCode.POSTURE,
                                level = 2,
                                comment = "합성 코멘트",
                            ),
                )

            assertRejected(axes = validAxes(), submission = missingRating)
            assertRejected(axes = validAxes(), submission = unexpectedRating)
        }

    @Test
    fun `평가 단계가 1에서 4 사이가 아니면 제출하지 않는다`() =
        runTest {
            listOf(0, 5).forEach { level ->
                val invalid =
                    validSubmission().copy(
                        ratings =
                            validSubmission().ratings.mapIndexed { index, rating ->
                                if (index == 0) rating.copy(level = level) else rating
                            },
                    )

                assertRejected(axes = validAxes(), submission = invalid)
            }
        }

    @Test
    fun `코멘트 길이는 문자열 length 100까지 허용하고 초과하면 제출하지 않는다`() =
        runTest {
            val repository = FakeGuestFeedbackRepository()
            val allowed = submissionWithFirstComment("😀".repeat(50))
            val rejected = submissionWithFirstComment("😀".repeat(51))

            val allowedResult =
                SubmitGuestFeedbackUseCase(repository)(
                    token = SYNTHETIC_TOKEN,
                    axes = validAxes(),
                    submission = allowed,
                )

            assertTrue(allowedResult.isSuccess)
            assertEquals(1, repository.submitCallCount)
            assertRejected(axes = validAxes(), submission = rejected)
        }

    @Test
    fun `저장소 오류는 실패 결과로 전달한다`() =
        runTest {
            val failure = IllegalStateException("synthetic failure")
            val repository = FakeGuestFeedbackRepository(failure = failure)

            val result = EnterGuestFeedbackUseCase(repository)(SYNTHETIC_TOKEN)

            assertSame(failure, result.exceptionOrNull())
        }

    @Test
    fun `저장소 취소 예외는 실패 결과로 감싸지 않는다`() =
        runTest {
            val repository =
                FakeGuestFeedbackRepository(
                    failure = CancellationException("synthetic cancellation"),
                )

            try {
                EnterGuestFeedbackUseCase(repository)(SYNTHETIC_TOKEN)
                fail("CancellationException이 다시 던져져야 합니다.")
            } catch (_: CancellationException) {
                Unit
            }
        }

    private suspend fun assertRejected(
        axes: List<GuestFeedbackAxis>,
        submission: GuestFeedbackSubmission,
    ) {
        val repository = FakeGuestFeedbackRepository()

        val result =
            SubmitGuestFeedbackUseCase(repository)(
                token = SYNTHETIC_TOKEN,
                axes = axes,
                submission = submission,
            )

        assertTrue(result.exceptionOrNull() is GuestFeedbackValidationException)
        assertEquals(0, repository.submitCallCount)
        assertNull(repository.submittedSubmission)
    }

    private class FakeGuestFeedbackRepository(
        val entry: GuestFeedbackEntry =
            GuestFeedbackEntry.Unavailable(
                GuestFeedbackUnavailableReason.FULL,
            ),
        private val failure: Throwable? = null,
    ) : GuestFeedbackRepository {
        var enterCallCount = 0
            private set
        var submitCallCount = 0
            private set
        var enteredToken: String? = null
            private set
        var submittedToken: String? = null
            private set
        var submittedSubmission: GuestFeedbackSubmission? = null
            private set

        override suspend fun enter(token: String): GuestFeedbackEntry {
            failure?.let { throw it }
            enterCallCount += 1
            enteredToken = token
            return entry
        }

        override suspend fun submit(
            token: String,
            submission: GuestFeedbackSubmission,
        ) {
            failure?.let { throw it }
            submitCallCount += 1
            submittedToken = token
            submittedSubmission = submission
        }
    }

    private companion object {
        const val SYNTHETIC_TOKEN = "synthetic-token"

        fun validAxes(): List<GuestFeedbackAxis> =
            listOf(
                GuestFeedbackAxis(GuestFeedbackAxisCode.GAZE, "시선"),
                GuestFeedbackAxis(GuestFeedbackAxisCode.VOICE, "목소리 크기"),
            )

        fun validSubmission(): GuestFeedbackSubmission =
            GuestFeedbackSubmission(
                nickname = "합성 지인",
                ratings =
                    listOf(
                        GuestFeedbackRating(GuestFeedbackAxisCode.GAZE, 1, "합성 코멘트"),
                        GuestFeedbackRating(GuestFeedbackAxisCode.VOICE, 4, "다른 코멘트"),
                    ),
            )

        fun submissionWithFirstComment(comment: String): GuestFeedbackSubmission =
            validSubmission().copy(
                ratings =
                    validSubmission().ratings.mapIndexed { index, rating ->
                        if (index == 0) rating.copy(comment = comment) else rating
                    },
            )
    }
}

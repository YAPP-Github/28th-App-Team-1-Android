package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.dto.interview.CreateInterviewSessionRequestDto
import com.dminus14.app.data.remote.dto.interview.FeedbackShareCreateResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewAbandonResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewReportListResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewReportResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewResumeConfirmResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewResumeStatusResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewSessionResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewSessionStatusResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoCompleteRequestDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoExpiryResponseDto
import com.dminus14.app.data.remote.dto.interview.InterviewVideoUploadUrlResponseDto
import com.dminus14.app.data.remote.dto.interview.JdValidateResponseDto
import com.dminus14.app.data.remote.dto.interview.SubmitAnswerResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.EmptyAttitudeAxesException
import com.dminus14.app.domain.exception.FeedbackShareAlreadyExistsException
import com.dminus14.app.domain.exception.FeedbackShareNotFoundException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidAttitudeAxisException
import com.dminus14.app.domain.exception.InvalidFeedbackShareStatusException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.TooManyAttitudeAxesException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class FeedbackShareRepositoryImplTest {
    @Test
    fun `링크 생성 성공 시 token 을 반환한다`() {
        val repository = FeedbackShareRepositoryImpl(FakeInterviewRemoteDataSource())

        val token =
            runBlocking { repository.createShare(1L, listOf(GuestFeedbackAxisCode.GAZE)) }

        assertEquals("fake-token", token)
    }

    @Test
    fun `링크 생성 400_404_409 비즈니스 코드를 전용 도메인 예외로 변환한다`() {
        val cases =
            listOf(
                Triple(
                    400,
                    ApiErrorCode.EMPTY_ATTITUDE_AXES,
                    EmptyAttitudeAxesException::class.java,
                ),
                Triple(
                    400,
                    ApiErrorCode.TOO_MANY_ATTITUDE_AXES,
                    TooManyAttitudeAxesException::class.java,
                ),
                Triple(
                    400,
                    ApiErrorCode.INVALID_ATTITUDE_AXIS,
                    InvalidAttitudeAxisException::class.java,
                ),
                Triple(
                    404,
                    ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND,
                    InterviewSessionNotFoundException::class.java,
                ),
                Triple(
                    409,
                    ApiErrorCode.FEEDBACK_SHARE_ALREADY_EXISTS,
                    FeedbackShareAlreadyExistsException::class.java,
                ),
            )

        cases.forEach { (status, code, expectedType) ->
            val httpError = httpException(status, code)
            val dataSource = FakeInterviewRemoteDataSource(createFailure = httpError)
            val repository = FeedbackShareRepositoryImpl(dataSource)

            val actual =
                captureFailure { repository.createShare(1L, listOf(GuestFeedbackAxisCode.GAZE)) }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `링크 종료 성공 시 예외 없이 완료된다`() {
        val repository = FeedbackShareRepositoryImpl(FakeInterviewRemoteDataSource())

        runBlocking { repository.closeShare(1L) }
    }

    @Test
    fun `링크 종료 400_404 비즈니스 코드를 전용 도메인 예외로 변환한다`() {
        val cases =
            listOf(
                Triple(
                    400,
                    ApiErrorCode.INVALID_SHARE_STATUS,
                    InvalidFeedbackShareStatusException::class.java,
                ),
                Triple(
                    404,
                    ApiErrorCode.FEEDBACK_SHARE_NOT_FOUND,
                    FeedbackShareNotFoundException::class.java,
                ),
            )

        cases.forEach { (status, code, expectedType) ->
            val httpError = httpException(status, code)
            val repository =
                FeedbackShareRepositoryImpl(FakeInterviewRemoteDataSource(closeFailure = httpError))

            val actual = captureFailure { repository.closeShare(1L) }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
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
                FeedbackShareRepositoryImpl(FakeInterviewRemoteDataSource(createFailure = failure))

            val actual =
                captureFailure { repository.createShare(1L, listOf(GuestFeedbackAxisCode.GAZE)) }

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
        val body: ResponseBody =
            """
            {"success":false,"code":"$code","message":"synthetic $code"}
            """.trimIndent()
                .toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(status, body))
    }

    @Suppress("TooManyFunctions")
    private class FakeInterviewRemoteDataSource(
        private val createFailure: Throwable? = null,
        private val closeFailure: Throwable? = null,
    ) : InterviewRemoteDataSource {
        override suspend fun validateJdUrl(jdUrl: String): JdValidateResponseDto =
            error("not needed for this test")

        override suspend fun createInterviewSession(
            request: CreateInterviewSessionRequestDto,
        ): InterviewSessionResponseDto = error("not needed for this test")

        override suspend fun getInterviewSessionStatus(
            sessionId: Long,
        ): InterviewSessionStatusResponseDto = error("not needed for this test")

        override suspend fun getReportList(): InterviewReportListResponseDto =
            error("not needed for this test")

        @Suppress("LongParameterList")
        override suspend fun submitAnswer(
            sessionId: Long,
            questionId: Long,
            isWrapUp: Boolean,
            questionAudioStartAt: Float?,
            questionAudioEndAt: Float?,
            answerStartAt: Float?,
            answerEndAt: Float?,
            answerDuration: Float?,
            endType: String?,
            audio: okhttp3.MultipartBody.Part?,
        ): SubmitAnswerResponseDto = error("not needed for this test")

        override suspend fun streamAudio(
            sessionId: Long,
            questionId: Long,
        ): ResponseBody = error("not needed for this test")

        override suspend fun getResume(sessionId: Long): InterviewResumeStatusResponseDto =
            error("not needed for this test")

        override suspend fun confirmResume(sessionId: Long): InterviewResumeConfirmResponseDto =
            error("not needed for this test")

        override suspend fun abandon(
            sessionId: Long,
            request: InterviewAbandonRequestDto?,
        ): InterviewAbandonResponseDto = error("not needed for this test")

        override suspend fun getReport(sessionId: Long): InterviewReportResponseDto =
            error("not needed for this test")

        override suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrlResponseDto =
            error("not needed for this test")

        override suspend fun completeUpload(
            sessionId: Long,
            request: InterviewVideoCompleteRequestDto?,
        ) = error("not needed for this test")

        override suspend fun getExpiry(sessionId: Long): InterviewVideoExpiryResponseDto =
            error("not needed for this test")

        override suspend fun createFeedbackShare(
            sessionId: Long,
            axes: List<String>,
        ): FeedbackShareCreateResponseDto {
            createFailure?.let { throw it }
            return FeedbackShareCreateResponseDto(token = "fake-token")
        }

        override suspend fun closeFeedbackShare(sessionId: Long) {
            closeFailure?.let { throw it }
        }
    }
}

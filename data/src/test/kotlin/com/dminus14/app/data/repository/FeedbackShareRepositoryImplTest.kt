package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.FeedbackShareRemoteDataSource
import com.dminus14.app.data.remote.dto.FeedbackShareCreateResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareStatusDto
import com.dminus14.app.data.remote.dto.FeedbackShareStatusResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackAxisCodeDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.EmptyAttitudeAxesException
import com.dminus14.app.domain.exception.FeedbackShareAlreadyExistsException
import com.dminus14.app.domain.exception.FeedbackShareNotFoundException
import com.dminus14.app.domain.exception.InterviewSessionNotFoundException
import com.dminus14.app.domain.exception.InvalidAttitudeAxisException
import com.dminus14.app.domain.exception.InvalidShareStatusException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.TooManyAttitudeAxesException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.FeedbackShare
import com.dminus14.app.domain.model.FeedbackShareStatus
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.google.gson.JsonParseException
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

class FeedbackShareRepositoryImplTest {
    @Test
    fun `상태 조회 호출을 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakeFeedbackShareRemoteDataSource()
        val repository = FeedbackShareRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getStatus(SESSION_ID) }

        assertEquals(
            FeedbackShare(
                token = "synthetic-token",
                status = FeedbackShareStatus.ACTIVE,
                axes = listOf(GuestFeedbackAxisCode.GAZE, GuestFeedbackAxisCode.VOICE),
                submittedCount = 2,
                videoExpiresAt = null,
                requestedAt = null,
            ),
            actual,
        )
        assertEquals(SESSION_ID, dataSource.statusSessionId)
    }

    @Test
    fun `생성 시 도메인 항목을 DTO로 변환하고 생성된 토큰을 반환한다`() {
        val dataSource = FakeFeedbackShareRemoteDataSource()
        val repository = FeedbackShareRepositoryImpl(dataSource)

        val actual =
            runBlocking {
                repository.create(
                    SESSION_ID,
                    listOf(GuestFeedbackAxisCode.POSTURE, GuestFeedbackAxisCode.GESTURE),
                )
            }

        assertEquals("synthetic-created-token", actual)
        assertEquals(SESSION_ID, dataSource.createSessionId)
        assertEquals(
            listOf(GuestFeedbackAxisCodeDto.POSTURE, GuestFeedbackAxisCodeDto.GESTURE),
            dataSource.createdAxes,
        )
    }

    @Test
    fun `비공개 전환 호출을 위임한다`() {
        val dataSource = FakeFeedbackShareRemoteDataSource()
        val repository = FeedbackShareRepositoryImpl(dataSource)

        runBlocking { repository.makePrivate(SESSION_ID) }

        assertEquals(SESSION_ID, dataSource.makePrivateSessionId)
    }

    @Test
    fun `여섯 비즈니스 오류 코드를 여섯 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.FEEDBACK_SHARE_NOT_FOUND to FeedbackShareNotFoundException::class.java,
                ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND to
                    InterviewSessionNotFoundException::class.java,
                ApiErrorCode.FEEDBACK_SHARE_ALREADY_EXISTS to
                    FeedbackShareAlreadyExistsException::class.java,
                ApiErrorCode.EMPTY_ATTITUDE_AXES to EmptyAttitudeAxesException::class.java,
                ApiErrorCode.TOO_MANY_ATTITUDE_AXES to TooManyAttitudeAxesException::class.java,
                ApiErrorCode.INVALID_ATTITUDE_AXIS to InvalidAttitudeAxisException::class.java,
                ApiErrorCode.INVALID_SHARE_STATUS to InvalidShareStatusException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(409, code)
            val repository =
                FeedbackShareRepositoryImpl(
                    FakeFeedbackShareRemoteDataSource(failure = httpError),
                )

            val actual = captureFailure { repository.getStatus(SESSION_ID) }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(
                code,
                (actual as com.dminus14.app.domain.exception.CustomException).errCode,
            )
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
                JsonParseException("synthetic invalid response") to UnknownException::class.java,
            )

        cases.forEach { (failure, expectedType) ->
            val repository =
                FeedbackShareRepositoryImpl(
                    FakeFeedbackShareRemoteDataSource(failure = failure),
                )

            val actual = captureFailure { repository.getStatus(SESSION_ID) }

            assertTrue(expectedType.isInstance(actual))
            assertSame(failure, actual.cause)
        }
    }

    private fun captureFailure(block: suspend () -> Unit): Throwable =
        try {
            runBlocking { block() }
            throw AssertionError("예외가 발생해야 합니다.")
        } catch (error: Throwable) {
            error
        }

    private fun httpException(
        status: Int,
        code: String,
    ): HttpException {
        val body =
            """{"success":false,"code":"$code","message":"합성 오류"}"""
                .toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(status, body))
    }

    private class FakeFeedbackShareRemoteDataSource(
        private val statusResponse: FeedbackShareStatusResponseDto = statusResponse(),
        private val createResponse: FeedbackShareCreateResponseDto =
            FeedbackShareCreateResponseDto(token = "synthetic-created-token"),
        private val failure: Throwable? = null,
    ) : FeedbackShareRemoteDataSource {
        var statusSessionId: Long? = null
            private set
        var createSessionId: Long? = null
            private set
        var createdAxes: List<GuestFeedbackAxisCodeDto>? = null
            private set
        var makePrivateSessionId: Long? = null
            private set

        override suspend fun getStatus(sessionId: Long): FeedbackShareStatusResponseDto {
            failure?.let { throw it }
            statusSessionId = sessionId
            return statusResponse
        }

        override suspend fun create(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCodeDto>,
        ): FeedbackShareCreateResponseDto {
            failure?.let { throw it }
            createSessionId = sessionId
            createdAxes = axes
            return createResponse
        }

        override suspend fun makePrivate(sessionId: Long) {
            failure?.let { throw it }
            makePrivateSessionId = sessionId
        }
    }

    private companion object {
        const val SESSION_ID = 41L

        fun statusResponse() =
            FeedbackShareStatusResponseDto(
                token = "synthetic-token",
                status = FeedbackShareStatusDto.ACTIVE,
                axes = listOf(GuestFeedbackAxisCodeDto.GAZE, GuestFeedbackAxisCodeDto.VOICE),
                submittedCount = 2,
                videoExpiresAt = null,
                requestedAt = null,
            )
    }
}

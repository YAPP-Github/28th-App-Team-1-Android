package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.UserRemoteDataSource
import com.dminus14.app.data.remote.dto.user.JobListResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.InvalidJobRoleException
import com.dminus14.app.domain.exception.NameAlreadyTakenException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.SocialReconnectRequiredException
import com.dminus14.app.domain.exception.SocialUnlinkFailedException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate
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

class UserRepositoryImplTest {
    @Test
    fun `프로필 조회 응답을 null 손실 없이 도메인 모델로 반환한다`() =
        runBlocking {
            val dataSource =
                FakeUserRemoteDataSource(
                    profileResponse = UserProfileFetchResponseDto(name = "합성 사용자"),
                )
            val repository = UserRepositoryImpl(dataSource)

            val actual = repository.getUserProfile()

            assertEquals(
                UserProfile("합성 사용자", null, null, null, null, null, null),
                actual,
            )
            assertEquals(1, dataSource.profileCallCount)
        }

    @Test
    fun `프로필 수정 모델을 DTO로 변환해 원격 데이터 소스에 전달한다`() =
        runBlocking {
            val dataSource = FakeUserRemoteDataSource()
            val repository = UserRepositoryImpl(dataSource)
            val update = UserProfileUpdate(name = "합성 사용자", jobRole = "BACKEND", careerYears = 3)

            val actual = repository.updateUserProfile(update)

            assertSame(Unit, actual)
            assertEquals(
                UserProfileUpdateRequestDto(name = "합성 사용자", jobRole = "BACKEND", careerYears = 3),
                dataSource.updateRequest,
            )
        }

    @Test
    fun `회원 탈퇴를 원격 데이터 소스에 위임한다`() =
        runBlocking {
            val dataSource = FakeUserRemoteDataSource()
            val repository = UserRepositoryImpl(dataSource)

            val actual = repository.withdraw()

            assertSame(Unit, actual)
            assertEquals(1, dataSource.withdrawalCallCount)
        }

    @Test
    fun `프로필 수정의 알려진 오류 코드를 도메인 예외로 변환한다`() =
        runBlocking {
            val cases =
                listOf(
                    "VALIDATION_ERROR" to ValidationException::class.java,
                    "INVALID_JOB_ROLE" to InvalidJobRoleException::class.java,
                    "NAME_ALREADY_TAKEN" to NameAlreadyTakenException::class.java,
                    "USER_NOT_FOUND" to UserNotFoundException::class.java,
                )

            cases.forEach { (code, expectedType) ->
                val httpError = httpException(400, code)
                val repository =
                    UserRepositoryImpl(FakeUserRemoteDataSource(updateFailure = httpError))

                val actual =
                    captureFailure {
                        repository.updateUserProfile(
                            UserProfileUpdate("합성 사용자", "BACKEND", 3),
                        )
                    }

                assertTrue(expectedType.isInstance(actual))
                assertEquals(code, (actual as CustomException).errCode)
                assertSame(httpError, actual.cause)
            }
        }

    @Test
    fun `회원 탈퇴의 알려진 오류 코드를 도메인 예외로 변환한다`() =
        runBlocking {
            val cases =
                listOf(
                    Triple(404, "USER_NOT_FOUND", UserNotFoundException::class.java),
                    Triple(
                        409,
                        "SOCIAL_RECONNECT_REQUIRED",
                        SocialReconnectRequiredException::class.java,
                    ),
                    Triple(502, "SOCIAL_UNLINK_FAILED", SocialUnlinkFailedException::class.java),
                )

            cases.forEach { (status, code, expectedType) ->
                val httpError = httpException(status, code)
                val repository =
                    UserRepositoryImpl(FakeUserRemoteDataSource(withdrawalFailure = httpError))

                val actual = captureFailure { repository.withdraw() }

                assertTrue(expectedType.isInstance(actual))
                assertEquals(code, (actual as CustomException).errCode)
                assertSame(httpError, actual.cause)
            }
        }

    @Test
    fun `전송 서버 알 수 없는 오류에 공통 오류 정책을 적용한다`() =
        runBlocking {
            val cases =
                listOf(
                    IOException("synthetic offline") to NetworkUnavailableException::class.java,
                    httpException(500, "SYNTHETIC_SERVER") to ServerException::class.java,
                    IllegalStateException(
                        "synthetic invalid state",
                    ) to UnknownException::class.java,
                )

            cases.forEach { (failure, expectedType) ->
                val repository =
                    UserRepositoryImpl(FakeUserRemoteDataSource(profileFailure = failure))

                val actual = captureFailure { repository.getUserProfile() }

                assertTrue(expectedType.isInstance(actual))
                assertSame(failure, actual.cause)
            }
        }

    private suspend fun captureFailure(block: suspend () -> Unit): Throwable =
        try {
            block()
            throw AssertionError("예외가 발생해야 합니다.")
        } catch (error: Throwable) {
            error
        }

    private class FakeUserRemoteDataSource(
        private val profileResponse: UserProfileFetchResponseDto =
            UserProfileFetchResponseDto(name = "합성 사용자"),
        private val profileFailure: Throwable? = null,
        private val updateFailure: Throwable? = null,
        private val withdrawalFailure: Throwable? = null,
    ) : UserRemoteDataSource {
        var profileCallCount = 0
            private set
        var withdrawalCallCount = 0
            private set
        var updateRequest: UserProfileUpdateRequestDto? = null
            private set

        override suspend fun getUserProfile(): UserProfileFetchResponseDto {
            profileFailure?.let { throw it }
            profileCallCount += 1
            return profileResponse
        }

        override suspend fun updateUserProfile(request: UserProfileUpdateRequestDto) {
            updateFailure?.let { throw it }
            updateRequest = request
        }

        override suspend fun withdraw() {
            withdrawalFailure?.let { throw it }
            withdrawalCallCount += 1
        }

        // 직군 목록 API 케이스 커버리지는 별도 테스트에서 다루고, 여기서는 컴파일 통과용 스텁만 둔다.
        override suspend fun getJobs(): JobListResponseDto = JobListResponseDto(jobs = emptyList())
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
}

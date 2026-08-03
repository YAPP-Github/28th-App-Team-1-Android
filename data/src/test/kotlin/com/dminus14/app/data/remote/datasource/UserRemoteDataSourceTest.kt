package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.UserApi
import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateResponseDto
import com.dminus14.app.domain.exception.ServerException
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class UserRemoteDataSourceTest {
    @Test
    fun `프로필 조회를 API에 위임하고 응답 DTO를 그대로 반환한다`() =
        runBlocking {
            val api = FakeUserApi()
            val dataSource = UserRemoteDataSourceImpl(api)

            val actual = dataSource.getUserProfile()

            assertSame(api.profileResponse, actual)
            assertEquals(1, api.profileCallCount)
        }

    @Test
    fun `프로필 수정 요청을 API에 위임하고 성공 응답을 Unit으로 처리한다`() =
        runBlocking {
            val api = FakeUserApi()
            val dataSource = UserRemoteDataSourceImpl(api)
            val request = UserProfileUpdateRequestDto(null, "BACKEND", 3)

            val actual = dataSource.updateUserProfile(request)

            assertSame(Unit, actual)
            assertSame(request, api.updateRequest)
        }

    @Test
    fun `프로필 수정 성공 응답이 false이면 서버 오류를 던진다`() =
        runBlocking {
            val api = FakeUserApi(updateResponse = UserProfileUpdateResponseDto(success = false))
            val dataSource = UserRemoteDataSourceImpl(api)

            val actual =
                captureFailure {
                    dataSource.updateUserProfile(UserProfileUpdateRequestDto(null, "BACKEND", 3))
                }

            assertTrue(actual is ServerException)
        }

    @Test
    fun `회원 탈퇴 204 응답을 Unit으로 처리한다`() =
        runBlocking {
            val api = FakeUserApi(withdrawalResponse = Response.success<Unit>(204, null))
            val dataSource = UserRemoteDataSourceImpl(api)

            val actual = dataSource.withdraw()

            assertSame(Unit, actual)
            assertEquals(1, api.withdrawalCallCount)
        }

    @Test
    fun `회원 탈퇴 실패 응답을 HttpException으로 전달한다`() =
        runBlocking {
            val api = FakeUserApi(withdrawalResponse = errorResponse(404))
            val dataSource = UserRemoteDataSourceImpl(api)

            val actual = captureFailure { dataSource.withdraw() }

            assertTrue(actual is HttpException)
            assertEquals(404, (actual as HttpException).code())
        }

    @Test
    fun `회원 탈퇴가 204 외 성공 코드이면 서버 오류를 던진다`() =
        runBlocking {
            val api = FakeUserApi(withdrawalResponse = Response.success(Unit))
            val dataSource = UserRemoteDataSourceImpl(api)

            val actual = captureFailure { dataSource.withdraw() }

            assertTrue(actual is ServerException)
        }

    private suspend fun captureFailure(block: suspend () -> Unit): Throwable =
        try {
            block()
            throw AssertionError("예외가 발생해야 합니다.")
        } catch (error: Throwable) {
            error
        }

    private class FakeUserApi(
        val profileResponse: UserProfileFetchResponseDto = UserProfileFetchResponseDto(),
        private val updateResponse: UserProfileUpdateResponseDto =
            UserProfileUpdateResponseDto(success = true),
        private val withdrawalResponse: Response<Unit> = Response.success<Unit>(204, null),
    ) : UserApi {
        var profileCallCount = 0
            private set
        var withdrawalCallCount = 0
            private set
        var updateRequest: UserProfileUpdateRequestDto? = null
            private set

        override suspend fun getProfile(): UserProfileFetchResponseDto {
            profileCallCount += 1
            return profileResponse
        }

        override suspend fun updateProfile(
            request: UserProfileUpdateRequestDto,
        ): UserProfileUpdateResponseDto {
            updateRequest = request
            return updateResponse
        }

        override suspend fun withdraw(): Response<Unit> {
            withdrawalCallCount += 1
            return withdrawalResponse
        }
    }

    private companion object {
        fun errorResponse(status: Int): Response<Unit> =
            Response.error(
                status,
                """{"success":false,"code":"USER_NOT_FOUND","message":"합성 오류"}"""
                    .toResponseBody("application/json".toMediaType()),
            )
    }
}

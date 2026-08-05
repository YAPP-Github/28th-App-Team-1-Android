package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.UserApi
import com.dminus14.app.data.remote.dto.user.JobListResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/** 회원 프로필 Retrofit 호출과 성공 응답 확인을 담당하는 원격 데이터 소스다. */
@Singleton
class UserRemoteDataSourceImpl
    @Inject
    constructor(
        private val userApi: UserApi,
    ) : UserRemoteDataSource {
        override suspend fun getUserProfile(): UserProfileFetchResponseDto {
            val response = userApi.getProfile()
            return response.data.takeIf { response.success }
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "회원 프로필 조회 응답에 data가 없습니다.",
                )
        }

        override suspend fun updateUserProfile(request: UserProfileUpdateRequestDto) {
            val response = userApi.updateProfile(request)
            if (!response.success) {
                throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "회원 프로필 수정 응답이 실패를 나타냅니다.",
                )
            }
        }

        override suspend fun withdraw() {
            val response = userApi.withdraw()
            if (!response.isSuccessful) throw HttpException(response)
            if (response.code() != HTTP_NO_CONTENT) {
                throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "회원 탈퇴 성공 응답 코드가 올바르지 않습니다.",
                )
            }
        }

        override suspend fun getJobs(): JobListResponseDto = userApi.getJobs()

        private companion object {
            const val HTTP_NO_CONTENT = 204
        }
    }

package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.user.JobListResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto

/** Retrofit 세부 구성을 노출하지 않고 회원 프로필 원격 작업을 제공하는 data 계층 계약이다. */
interface UserRemoteDataSource {
    suspend fun getUserProfile(): UserProfileFetchResponseDto

    suspend fun updateUserProfile(request: UserProfileUpdateRequestDto)

    suspend fun withdraw()

    suspend fun getJobs(): JobListResponseDto
}

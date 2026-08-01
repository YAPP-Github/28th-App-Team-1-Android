package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApi {
    @GET("api/v1/users/me/profile")
    suspend fun getProfile(): UserProfileFetchResponseDto

    @PATCH("api/v1/users/me/profile")
    suspend fun updateProfile(
        @Body request: UserProfileUpdateRequestDto,
    ): UserProfileUpdateResponseDto

    @DELETE("api/v1/users/me")
    suspend fun withdraw(): Response<Unit>
}

package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.UserProfileDto
import retrofit2.http.GET

interface UserApi {
    @GET("api/v1/users/me/profile")
    suspend fun getUserProfile(): ApiResponseDto<UserProfileDto>
}

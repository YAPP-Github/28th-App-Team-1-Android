package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.ExamplePostRequestDto
import com.dminus14.app.data.remote.dto.ExamplePostResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("examples")
    suspend fun createExample(
        @Body request: ExamplePostRequestDto,
    ): ExamplePostResponseDto
}

package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 서버 성공 응답의 공통 래퍼.
 *
 * 형식: `{ "success": true, "data": { ... } }`
 */
data class ApiResponseDto<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: T?,
)

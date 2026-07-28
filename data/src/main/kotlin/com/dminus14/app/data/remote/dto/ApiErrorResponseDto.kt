package com.dminus14.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 서버가 공통으로 사용하는 에러 응답 포맷.
 *
 * 소셜 로그인/토큰 재발급뿐 아니라, [com.dminus14.app.data.remote.authenticator.TokenAuthenticator]가
 * 가로채는 모든 API의 401 응답 바디도 이 포맷을 따른다고 가정한다.
 */
data class ApiErrorResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
)

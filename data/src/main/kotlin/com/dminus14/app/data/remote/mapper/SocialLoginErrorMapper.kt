package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.SocialLoginErrorResponseDto
import com.dminus14.app.domain.model.KakaoAuthException
import com.google.gson.Gson
import retrofit2.HttpException

internal object SocialLoginErrorMapper {
    private val gson = Gson()

    fun mapHttpException(error: HttpException): KakaoAuthException {
        val apiError = parseErrorBody(error)

        return when (error.code()) {
            HTTP_BAD_REQUEST ->
                KakaoAuthException.Client(
                    message = apiError?.message ?: "유효하지 않은 인증 정보입니다.",
                    cause = error,
                )

            HTTP_UNAUTHORIZED ->
                KakaoAuthException.Client(
                    message = apiError?.message ?: "소셜 로그인에 실패했습니다.",
                    cause = error,
                )

            in HTTP_SERVER_ERROR_RANGE -> KakaoAuthException.Server(cause = error)

            else ->
                KakaoAuthException.Unknown(
                    message = apiError?.message.orEmpty(),
                    cause = error,
                )
        }
    }

    private fun parseErrorBody(error: HttpException): SocialLoginErrorResponseDto? =
        runCatching {
            error.response()?.errorBody()?.string()?.let { body ->
                gson.fromJson(body, SocialLoginErrorResponseDto::class.java)
            }
        }.getOrNull()

    private const val HTTP_BAD_REQUEST = 400
    private const val HTTP_UNAUTHORIZED = 401
    private val HTTP_SERVER_ERROR_RANGE = 500..599
}

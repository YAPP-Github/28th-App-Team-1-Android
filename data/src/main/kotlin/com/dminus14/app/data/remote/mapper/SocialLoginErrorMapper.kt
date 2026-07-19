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
            HTTP_BAD_REQUEST -> {
                KakaoAuthException.Client(
                    message = apiError?.message ?: "유효하지 않은 인증 정보입니다.",
                    cause = error,
                )
            }

            HTTP_UNAUTHORIZED -> {
                KakaoAuthException.Client(
                    message = apiError?.message ?: "소셜 로그인에 실패했습니다.",
                    cause = error,
                )
            }

            in HTTP_SERVER_ERROR_RANGE -> {
                KakaoAuthException.Server(cause = error)
            }

            else -> {
                KakaoAuthException.Unknown(
                    message = apiError?.message.orEmpty(),
                    cause = error,
                )
            }
        }
    }

    /**
     * RefreshToken 자체가 만료되어 재로그인이 필요한 경우([CODE_LOGIN_EXPIRED])인지 판별한다.
     *
     * [AuthRemoteDataSource]의 refresh 응답 처리에서 사용되며, 에러 바디 파싱 로직을
     * [mapHttpException]과 공유해 중복 파싱을 피한다.
     */
    fun isLoginExpired(error: HttpException): Boolean = parseErrorBody(error)?.code == CODE_LOGIN_EXPIRED

    private fun parseErrorBody(error: HttpException): SocialLoginErrorResponseDto? =
        runCatching {
            error.response()?.errorBody()?.string()?.let { body ->
                gson.fromJson(body, SocialLoginErrorResponseDto::class.java)
            }
        }.getOrNull()

    private const val HTTP_BAD_REQUEST = 400
    private const val HTTP_UNAUTHORIZED = 401
    private val HTTP_SERVER_ERROR_RANGE = 500..599
    private const val CODE_LOGIN_EXPIRED = "LOGIN_EXPIRED"
}

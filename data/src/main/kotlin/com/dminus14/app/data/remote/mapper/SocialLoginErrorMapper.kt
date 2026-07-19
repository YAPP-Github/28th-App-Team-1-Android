package com.dminus14.app.data.remote.mapper

import com.dminus14.app.domain.model.KakaoAuthException
import retrofit2.HttpException

/**
 * 소셜 로그인 API(`/api/v1/auth/social/login`) 실패 응답을 [KakaoAuthException]으로 변환한다.
 *
 * 스펙상 클라이언트 오류 코드:
 * - 400 [ApiErrorCode.INVALID_CREDENTIAL]
 * - 401 [ApiErrorCode.SOCIAL_LOGIN_FAILED]
 */
internal object SocialLoginErrorMapper {
    fun mapHttpException(error: HttpException): KakaoAuthException {
        val apiError = ApiErrorBodyParser.parse(error)

        return when (apiError?.code) {
            ApiErrorCode.INVALID_CREDENTIAL -> {
                KakaoAuthException.Client(
                    message = apiError.message.ifBlank { "유효하지 않은 인증 정보입니다." },
                    cause = error,
                )
            }

            ApiErrorCode.SOCIAL_LOGIN_FAILED -> {
                KakaoAuthException.Client(
                    message = apiError.message.ifBlank { "소셜 로그인에 실패했습니다." },
                    cause = error,
                )
            }

            else -> {
                when (error.code()) {
                    in HTTP_SERVER_ERROR_RANGE -> KakaoAuthException.Server(cause = error)

                    else -> {
                        KakaoAuthException.Unknown(
                            message = apiError?.message.orEmpty(),
                            cause = error,
                        )
                    }
                }
            }
        }
    }

    private val HTTP_SERVER_ERROR_RANGE = 500..599
}

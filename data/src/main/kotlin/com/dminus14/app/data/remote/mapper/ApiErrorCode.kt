package com.dminus14.app.data.remote.mapper

/**
 * API / 클라이언트 에러 `code` 값.
 *
 * - 일반 API 401: [TOKEN_EXPIRED], [INVALID_TOKEN] → 토큰 재발급 후 재시도
 * - 재발급 API 401: [LOGIN_EXPIRED] → 재로그인 필요
 * - 소셜 로그인 400/401: [INVALID_CREDENTIAL], [SOCIAL_LOGIN_FAILED]
 * - 재발급 API 400: [VALIDATION_ERROR]
 * - 회원 프로필 조회 404: [USER_NOT_FOUND]
 * - 클라이언트 분류: [NETWORK_UNAVAILABLE], [SERVER_ERROR], [UNKNOWN]
 */
internal object ApiErrorCode {
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    const val INVALID_TOKEN = "INVALID_TOKEN"
    const val LOGIN_EXPIRED = "LOGIN_EXPIRED"

    const val INVALID_CREDENTIAL = "INVALID_CREDENTIAL"
    const val SOCIAL_LOGIN_FAILED = "SOCIAL_LOGIN_FAILED"

    const val VALIDATION_ERROR = "VALIDATION_ERROR"

    const val USER_NOT_FOUND = "USER_NOT_FOUND"

    const val NETWORK_UNAVAILABLE = "NETWORK_UNAVAILABLE"
    const val SERVER_ERROR = "SERVER_ERROR"
    const val UNKNOWN = "UNKNOWN"

    /** 다른 API에서 AccessToken 문제로 재발급을 호출해야 하는 코드. */
    fun requiresTokenRefresh(code: String?): Boolean =
        code == TOKEN_EXPIRED || code == INVALID_TOKEN
}

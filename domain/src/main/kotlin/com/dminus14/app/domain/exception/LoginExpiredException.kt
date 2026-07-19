package com.dminus14.app.domain.exception

/**
 * RefreshToken 자체가 만료되어 재로그인이 필요한 경우 발생하는 예외.
 *
 * AccessToken 재발급(`/api/v1/auth/token/refresh`) 호출이 `LOGIN_EXPIRED` 코드로 실패했을 때
 * 이 예외로 변환되어 상위(`TokenAuthenticator`)에 전달된다.
 */
class LoginExpiredException(
    message: String = "로그인 세션이 만료되었습니다. 다시 로그인해 주세요.",
    cause: Throwable? = null,
) : Exception(message, cause)

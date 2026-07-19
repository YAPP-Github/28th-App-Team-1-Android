package com.dminus14.app.domain.exception

/**
 * 우리 서버 소셜 로그인 API(`/api/v1/auth/social/login`) 실패.
 *
 * 카카오/애플 SDK 오류는 feature 레이어에서 다루며, 이 타입에는 포함하지 않는다.
 */
sealed class SocialLoginException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /** HTTP 4xx — INVALID_CREDENTIAL, SOCIAL_LOGIN_FAILED 등. */
    class Client(
        message: String,
        cause: Throwable? = null,
    ) : SocialLoginException(message, cause)

    class Network(
        message: String = "네트워크 연결을 확인해 주세요.",
        cause: Throwable? = null,
    ) : SocialLoginException(message, cause)

    class Server(
        message: String = "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
        cause: Throwable? = null,
    ) : SocialLoginException(message, cause)

    class Unknown(
        message: String = "알 수 없는 오류가 발생했습니다.",
        cause: Throwable? = null,
    ) : SocialLoginException(message, cause)
}

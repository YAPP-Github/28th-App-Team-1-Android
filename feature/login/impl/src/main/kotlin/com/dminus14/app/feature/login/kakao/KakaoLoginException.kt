package com.dminus14.app.feature.login.kakao

/**
 * 카카오 SDK 로그인 실패.
 *
 * Screen에서 [KakaoLoginClient]를 통해 발생하며, 우리 서버 API 예외와는 분리한다.
 */
sealed class KakaoLoginException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /** 사용자가 로그인 화면에서 취소한 경우. */
    data object Cancelled : KakaoLoginException("로그인이 취소되었습니다.")

    /** 동의 화면에서 취소하거나 필수 항목에 동의하지 않은 경우. */
    data object AccessDenied : KakaoLoginException("로그인 동의가 필요합니다.")

    class Client(
        message: String,
        cause: Throwable? = null,
    ) : KakaoLoginException(message, cause)

    class Server(
        message: String = "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
        cause: Throwable? = null,
    ) : KakaoLoginException(message, cause)

    class Unknown(
        message: String = "알 수 없는 오류가 발생했습니다.",
        cause: Throwable? = null,
    ) : KakaoLoginException(message, cause)
}

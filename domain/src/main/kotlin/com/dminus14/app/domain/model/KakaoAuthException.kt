package com.dminus14.app.domain.model

sealed class KakaoAuthException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * [Kakao] ClientError(Cancelled)
     * 카카오톡 앱 디바이스 권한 제공 화면에서 뒤로가기한 경우
     */
    data object Cancelled : KakaoAuthException("로그인이 취소되었습니다.")

    /**
     * [Kakao] AuthError(AccessDenied)
     * 동의 화면에서 취소하거나 필수 항목에 동의하지 않은 경우
     */
    data object AccessDenied : KakaoAuthException("로그인 동의가 필요합니다.")

    /**
     * [Backend / Kakao SDK] HTTP 4xx 또는 기타 클라이언트 설정 오류
     */
    class Client(
        message: String,
        cause: Throwable? = null,
    ) : KakaoAuthException(message, cause)

    /**
     * [Infrastructure] 네트워크 연결 오류
     */
    class Network(
        message: String = "네트워크 연결을 확인해 주세요.",
        cause: Throwable? = null,
    ) : KakaoAuthException(message, cause)

    /**
     * [Infrastructure] HTTP 5xx 또는 카카오 서버 오류
     */
    class Server(
        message: String = "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
        cause: Throwable? = null,
    ) : KakaoAuthException(message, cause)

    /**
     * [Kakao] AuthError(Unknown) 및 분류되지 않은 오류
     */
    class Unknown(
        message: String = "알 수 없는 오류가 발생했습니다.",
        cause: Throwable? = null,
    ) : KakaoAuthException(message, cause)
}

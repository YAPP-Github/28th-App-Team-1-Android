package com.dminus14.app.domain.exception

/**
 * 프로젝트 공통 예외 루트.
 *
 * 하나의 Exception 타입은 하나의 의미(에러 코드)만 담당한다.
 * Network / Server / Unknown처럼 HTTP·전송 계층 분류와,
 * 서버 비즈니스 코드별 예외를 모두 이 계층 아래에 둔다.
 */
open class CustomException(
    val errCode: String,
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

class NetworkUnavailableException(
    errCode: String,
    message: String = "네트워크 연결을 확인해 주세요.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

class ServerException(
    errCode: String,
    message: String = "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

class UnknownException(
    errCode: String,
    message: String = "알 수 없는 오류가 발생했습니다.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 소셜 로그인 — 유효하지 않은 인증 정보 (`INVALID_CREDENTIAL`). */
class InvalidCredentialException(
    errCode: String,
    message: String = "유효하지 않은 인증 정보입니다.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 소셜 로그인 — 소셜 로그인 실패 (`SOCIAL_LOGIN_FAILED`). */
class SocialLoginFailedException(
    errCode: String,
    message: String = "소셜 로그인에 실패했습니다.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** RefreshToken 만료/무효로 재로그인이 필요한 경우 (`LOGIN_EXPIRED`). */
class SessionException(
    errCode: String,
    message: String = "로그인 세션이 만료되었습니다. 다시 로그인해 주세요.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 요청 유효성 검증 실패 (`VALIDATION_ERROR`). */
class ValidationException(
    errCode: String,
    message: String = "요청 값이 올바르지 않습니다.",
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

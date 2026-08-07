package com.dminus14.app.domain.exception

/** 지원하지 않는 플랫폼 값이다 (`INVALID_PLATFORM`). */
class InvalidPlatformException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 버전 문자열 형식이 올바르지 않다 (`INVALID_VERSION_FORMAT`). */
class InvalidVersionFormatException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

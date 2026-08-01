package com.dminus14.app.domain.exception

/** 호출 전에 Consent 입력 계약을 만족하지 못한 경우다. */
class ConsentValidationException(
    message: String,
) : CustomException(
        errCode = ERROR_CODE,
        message = message,
    ) {
    private companion object {
        const val ERROR_CODE = "CONSENT_VALIDATION_ERROR"
    }
}

/** 동의 항목 버전이 최신이 아니어서 목록을 다시 조회해야 하는 경우다. */
class ConsentVersionMismatchException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 필수 동의 항목이 누락되었거나 거부된 경우다. */
class RequiredConsentMissingException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 지원하지 않는 동의 항목 코드다. */
class InvalidConsentItemException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 요청한 동의 문서를 찾지 못한 경우다. */
class ConsentDocumentNotFoundException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

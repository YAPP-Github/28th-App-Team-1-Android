package com.dminus14.app.domain.exception

/** 호출 전에 FeedbackShare 입력 계약을 만족하지 못한 경우다. */
class FeedbackShareValidationException(
    message: String,
) : CustomException(errCode = ERROR_CODE, message = message) {
    private companion object {
        const val ERROR_CODE = "FEEDBACK_SHARE_VALIDATION_ERROR"
    }
}

/** 공유 링크가 없는 경우다 (`FEEDBACK_SHARE_NOT_FOUND`). */
class FeedbackShareNotFoundException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 이미 활성 공유 링크가 있어 재생성할 수 없는 경우다 (`FEEDBACK_SHARE_ALREADY_EXISTS`). */
class FeedbackShareAlreadyExistsException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 지정 평가 항목을 하나도 선택하지 않은 경우다 (`EMPTY_ATTITUDE_AXES`). */
class EmptyAttitudeAxesException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 지정 평가 항목이 5개를 초과한 경우다 (`TOO_MANY_ATTITUDE_AXES`). */
class TooManyAttitudeAxesException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 지원하지 않는 평가 항목을 지정한 경우다 (`INVALID_ATTITUDE_AXIS`). */
class InvalidAttitudeAxisException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 지원하지 않는 공유 상태 전환을 요청한 경우다 (`INVALID_SHARE_STATUS`). */
class InvalidShareStatusException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

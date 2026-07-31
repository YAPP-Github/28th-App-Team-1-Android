package com.dminus14.app.domain.exception

/** 호출 전에 Guest Feedback 입력 계약을 만족하지 못한 경우다. */
class GuestFeedbackValidationException(
    message: String,
) : CustomException(
        errCode = ERROR_CODE,
        message = message,
    ) {
    private companion object {
        const val ERROR_CODE = "GUEST_FEEDBACK_VALIDATION_ERROR"
    }
}

/** token 또는 제출 값 문제로 Guest 서버 요청을 처리하지 못한 경우다. */
class GuestFeedbackRequestException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 제출 시점에 공유가 비공개 또는 만료 상태로 바뀐 경우다. */
class GuestFeedbackShareClosedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 면접의 누적 피드백 인원이 최대치에 도달한 경우다. */
class GuestFeedbackCapacityFullException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 현재 설치 ID로 이미 피드백을 제출한 경우다. */
class GuestFeedbackAlreadySubmittedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

package com.dminus14.app.domain.exception

/**
 * 지인 피드백 공유 링크 생성(호스트) — 평가 항목을 하나도 선택하지 않은 경우
 * (`EMPTY_ATTITUDE_AXES`).
 */
class EmptyAttitudeAxesException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/**
 * 지인 피드백 공유 링크 생성(호스트) — 평가 항목을 5개보다 많이 선택한 경우
 * (`TOO_MANY_ATTITUDE_AXES`).
 */
class TooManyAttitudeAxesException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/**
 * 지인 피드백 공유 링크 생성(호스트) — 서버가 지원하지 않는 평가 항목이 포함된 경우
 * (`INVALID_ATTITUDE_AXIS`).
 */
class InvalidAttitudeAxisException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/**
 * 면접당 활성 공유 링크가 이미 있는 경우 (`FEEDBACK_SHARE_ALREADY_EXISTS`). 재생성은 서버가
 * 지원하지 않는다(feedback.md).
 */
class FeedbackShareAlreadyExistsException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 종료(PATCH)하려는 공유 링크가 서버에 없는 경우 (`FEEDBACK_SHARE_NOT_FOUND`). */
class FeedbackShareNotFoundException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/**
 * 공유 링크를 지원하지 않는 상태로 전환하려는 경우 (`INVALID_SHARE_STATUS`). 이미 비공개로
 * 전환된 링크를 다시 종료하려는 경우를 포함한다.
 */
class InvalidFeedbackShareStatusException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

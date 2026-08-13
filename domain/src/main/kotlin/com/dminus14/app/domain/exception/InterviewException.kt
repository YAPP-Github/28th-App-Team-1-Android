package com.dminus14.app.domain.exception

/** JD URL 형식이 올바르지 않은 경우다 (`/api/v1/jd/validate`, `INVALID_JD_URL`). */
class InvalidJdUrlException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 1일 JD 검증 성공 횟수(5회)를 초과한 경우다 (`JD_VALIDATION_LIMIT_EXCEEDED`). */
class JdValidationLimitExceededException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 직군·연차가 회원 프로필에 등록돼 있지 않은 경우다 (`USER_PROFILE_NOT_REGISTERED`). */
class UserProfileNotRegisteredException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** jdUrl과 jdText를 동시에 입력한 경우다 (`JD_URL_AND_TEXT_BOTH_PROVIDED`). */
class JdUrlAndTextBothProvidedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** jdUrl을 `/api/v1/jd/validate`로 먼저 검증하지 않은 경우다 (`JD_NOT_VALIDATED`). */
class JdNotValidatedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** JD 검증 캐시가 만료(6시간 TTL 경과)된 경우다 (`JD_CONTENT_NOT_FOUND`). */
class JdContentNotFoundException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** JD 본문 직접 입력 길이가 200~3,000자 범위를 벗어난 경우다 (`INVALID_JD_LENGTH`). */
class InvalidJdLengthException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 집중 프로젝트 설명 길이가 10~300자 범위를 벗어난 경우다 (`INVALID_FREETEXT_LENGTH`). */
class InvalidFreeTextLengthException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 집중 프로젝트 설명이 포트폴리오와 관련성이 낮은 경우다 (`FREETEXT_NOT_RELEVANT`). */
class FreeTextNotRelevantException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 면접 세션 생성에 쓸 남은 이용권이 없는 경우다 (`NO_REMAINING_TICKET`). */
class NoRemainingTicketException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 어뷰징 탐지로 면접 시작이 제한된 경우다 (`ACCOUNT_SUSPENDED`). */
class AccountSuspendedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 요청한 면접 세션을 찾지 못한 경우다 (`INTERVIEW_SESSION_NOT_FOUND`). */
class InterviewSessionNotFoundException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

class AiTemporarilyUnavailableException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode, message, cause)

class AnswerAlreadySubmittedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode, message, cause)

class InterviewSessionAlreadyEndedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode, message, cause)

class InterviewSessionNotStartedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode, message, cause)

class InterviewSessionPreloadFailedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode, message, cause)

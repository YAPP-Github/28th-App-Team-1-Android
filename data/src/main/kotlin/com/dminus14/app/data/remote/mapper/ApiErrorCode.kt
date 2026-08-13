package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.mapper.ApiErrorCode.ACCOUNT_SUSPENDED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.APP_VERSION_POLICY_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.CONSENT_VERSION_MISMATCH
import com.dminus14.app.data.remote.mapper.ApiErrorCode.DELETE_LIMIT_EXCEEDED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.DOCUMENT_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.EMPTY_ATTITUDE_AXES
import com.dminus14.app.data.remote.mapper.ApiErrorCode.FEEDBACK_SHARE_ALREADY_EXISTS
import com.dminus14.app.data.remote.mapper.ApiErrorCode.FEEDBACK_SHARE_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.FILE_TOO_LARGE
import com.dminus14.app.data.remote.mapper.ApiErrorCode.FREETEXT_NOT_RELEVANT
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INTERVIEW_SESSION_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_ATTITUDE_AXIS
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_CONSENT_ITEM
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_CREDENTIAL
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_FILE_TYPE
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_FREETEXT_LENGTH
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_JD_LENGTH
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_JD_URL
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_JOB_ROLE
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_PDF_FILE
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_PLATFORM
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_SHARE_STATUS
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_TOKEN
import com.dminus14.app.data.remote.mapper.ApiErrorCode.INVALID_VERSION_FORMAT
import com.dminus14.app.data.remote.mapper.ApiErrorCode.JD_CONTENT_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.JD_NOT_VALIDATED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.JD_URL_AND_TEXT_BOTH_PROVIDED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.JD_VALIDATION_LIMIT_EXCEEDED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.LOGIN_EXPIRED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.NAME_ALREADY_TAKEN
import com.dminus14.app.data.remote.mapper.ApiErrorCode.NETWORK_UNAVAILABLE
import com.dminus14.app.data.remote.mapper.ApiErrorCode.NO_REMAINING_TICKET
import com.dminus14.app.data.remote.mapper.ApiErrorCode.PAGE_COUNT_EXCEEDED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.PORTFOLIO_ALREADY_EXISTS
import com.dminus14.app.data.remote.mapper.ApiErrorCode.PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW
import com.dminus14.app.data.remote.mapper.ApiErrorCode.PORTFOLIO_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.PORTFOLIO_PROCESSING
import com.dminus14.app.data.remote.mapper.ApiErrorCode.PORTFOLIO_UPLOAD_FAILED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.REPLACEMENT_LIMIT_EXCEEDED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.REQUIRED_CONSENT_MISSING
import com.dminus14.app.data.remote.mapper.ApiErrorCode.SERVER_ERROR
import com.dminus14.app.data.remote.mapper.ApiErrorCode.SOCIAL_LOGIN_FAILED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.SOCIAL_RECONNECT_REQUIRED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.SOCIAL_UNLINK_FAILED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.TOKEN_EXPIRED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.TOO_MANY_ATTITUDE_AXES
import com.dminus14.app.data.remote.mapper.ApiErrorCode.UNKNOWN
import com.dminus14.app.data.remote.mapper.ApiErrorCode.USER_NOT_FOUND
import com.dminus14.app.data.remote.mapper.ApiErrorCode.USER_PROFILE_NOT_REGISTERED
import com.dminus14.app.data.remote.mapper.ApiErrorCode.VALIDATION_ERROR

/**
 * API / 클라이언트 에러 `code` 값.
 *
 * - 일반 API 401: [TOKEN_EXPIRED], [INVALID_TOKEN] → 토큰 재발급 후 재시도
 * - 재발급 API 401: [LOGIN_EXPIRED] → 재로그인 필요
 * - 소셜 로그인 400/401: [INVALID_CREDENTIAL], [SOCIAL_LOGIN_FAILED]
 * - 재발급 API 400: [VALIDATION_ERROR]
 * - User API: [USER_NOT_FOUND], [INVALID_JOB_ROLE], [NAME_ALREADY_TAKEN],
 *   [SOCIAL_RECONNECT_REQUIRED], [SOCIAL_UNLINK_FAILED]
 * - 동의 API: [CONSENT_VERSION_MISMATCH], [REQUIRED_CONSENT_MISSING],
 *   [INVALID_CONSENT_ITEM], [DOCUMENT_NOT_FOUND]
 * - JD validate API 400/429: [INVALID_JD_URL], [JD_VALIDATION_LIMIT_EXCEEDED]
 * - Portfolio register API 400/409: [INVALID_FILE_TYPE], [FILE_TOO_LARGE],
 *   [PAGE_COUNT_EXCEEDED], [INVALID_PDF_FILE], [PORTFOLIO_ALREADY_EXISTS],
 *   [REPLACEMENT_LIMIT_EXCEEDED]
 * - Portfolio status/delete API 404: [PORTFOLIO_NOT_FOUND]
 * - Portfolio delete API 409: [PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW], [DELETE_LIMIT_EXCEEDED]
 * - Interview session create API 400: [VALIDATION_ERROR], [USER_PROFILE_NOT_REGISTERED],
 *   [JD_URL_AND_TEXT_BOTH_PROVIDED], [JD_NOT_VALIDATED], [JD_CONTENT_NOT_FOUND],
 *   [INVALID_JD_LENGTH], [INVALID_FREETEXT_LENGTH], [FREETEXT_NOT_RELEVANT],
 *   [PORTFOLIO_PROCESSING], [PORTFOLIO_UPLOAD_FAILED]
 * - Interview session create API 403: [ACCOUNT_SUSPENDED], [NO_REMAINING_TICKET]
 * - Interview session create/status API 404: [PORTFOLIO_NOT_FOUND], [INTERVIEW_SESSION_NOT_FOUND]
 * - Feedback share 생성 API(POST) 400/404/409: [EMPTY_ATTITUDE_AXES], [TOO_MANY_ATTITUDE_AXES],
 *   [INVALID_ATTITUDE_AXIS], [INTERVIEW_SESSION_NOT_FOUND], [FEEDBACK_SHARE_ALREADY_EXISTS]
 * - Feedback share 종료 API(PATCH) 400/404: [INVALID_SHARE_STATUS], [FEEDBACK_SHARE_NOT_FOUND]
 * - 앱 버전 확인 API 400: [INVALID_PLATFORM], [INVALID_VERSION_FORMAT]
 * - 앱 버전 확인 API 404: [APP_VERSION_POLICY_NOT_FOUND] (사용자가 대응할 수 없는 서버 설정
 *   누락이므로 ServerError로 격상해 처리한다)
 * - 클라이언트 분류: [NETWORK_UNAVAILABLE], [SERVER_ERROR], [UNKNOWN]
 */
internal object ApiErrorCode {
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    const val INVALID_TOKEN = "INVALID_TOKEN"
    const val LOGIN_EXPIRED = "LOGIN_EXPIRED"

    const val INVALID_CREDENTIAL = "INVALID_CREDENTIAL"
    const val SOCIAL_LOGIN_FAILED = "SOCIAL_LOGIN_FAILED"

    const val VALIDATION_ERROR = "VALIDATION_ERROR"

    const val FEEDBACK_SHARE_TOKEN_NOT_FOUND = "FEEDBACK_SHARE_TOKEN_NOT_FOUND"
    const val INCOMPLETE_RATINGS = "INCOMPLETE_RATINGS"
    const val INVALID_RATING_LEVEL = "INVALID_RATING_LEVEL"
    const val MISSING_DEVICE_ID = "MISSING_DEVICE_ID"
    const val FEEDBACK_SHARE_CLOSED = "FEEDBACK_SHARE_CLOSED"
    const val FEEDBACK_CAPACITY_FULL = "FEEDBACK_CAPACITY_FULL"
    const val FEEDBACK_ALREADY_SUBMITTED = "FEEDBACK_ALREADY_SUBMITTED"

    // Feedback share 생성(POST)/종료(PATCH) API — 호스트(본인) 전용.
    const val EMPTY_ATTITUDE_AXES = "EMPTY_ATTITUDE_AXES"
    const val TOO_MANY_ATTITUDE_AXES = "TOO_MANY_ATTITUDE_AXES"
    const val INVALID_ATTITUDE_AXIS = "INVALID_ATTITUDE_AXIS"
    const val FEEDBACK_SHARE_ALREADY_EXISTS = "FEEDBACK_SHARE_ALREADY_EXISTS"
    const val FEEDBACK_SHARE_NOT_FOUND = "FEEDBACK_SHARE_NOT_FOUND"
    const val INVALID_SHARE_STATUS = "INVALID_SHARE_STATUS"

    const val USER_NOT_FOUND = "USER_NOT_FOUND"
    const val INVALID_JOB_ROLE = "INVALID_JOB_ROLE"
    const val NAME_ALREADY_TAKEN = "NAME_ALREADY_TAKEN"
    const val SOCIAL_RECONNECT_REQUIRED = "SOCIAL_RECONNECT_REQUIRED"
    const val SOCIAL_UNLINK_FAILED = "SOCIAL_UNLINK_FAILED"

    const val CONSENT_VERSION_MISMATCH = "CONSENT_VERSION_MISMATCH"
    const val REQUIRED_CONSENT_MISSING = "REQUIRED_CONSENT_MISSING"
    const val INVALID_CONSENT_ITEM = "INVALID_CONSENT_ITEM"
    const val DOCUMENT_NOT_FOUND = "DOCUMENT_NOT_FOUND"

    const val INVALID_JD_URL = "INVALID_JD_URL"
    const val JD_VALIDATION_LIMIT_EXCEEDED = "JD_VALIDATION_LIMIT_EXCEEDED"

    const val INVALID_FILE_TYPE = "INVALID_FILE_TYPE"
    const val FILE_TOO_LARGE = "FILE_TOO_LARGE"
    const val PAGE_COUNT_EXCEEDED = "PAGE_COUNT_EXCEEDED"
    const val INVALID_PDF_FILE = "INVALID_PDF_FILE"
    const val PORTFOLIO_ALREADY_EXISTS = "PORTFOLIO_ALREADY_EXISTS"
    const val REPLACEMENT_LIMIT_EXCEEDED = "REPLACEMENT_LIMIT_EXCEEDED"
    const val PORTFOLIO_NOT_FOUND = "PORTFOLIO_NOT_FOUND"
    const val PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW = "PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW"
    const val DELETE_LIMIT_EXCEEDED = "DELETE_LIMIT_EXCEEDED"

    const val USER_PROFILE_NOT_REGISTERED = "USER_PROFILE_NOT_REGISTERED"
    const val JD_URL_AND_TEXT_BOTH_PROVIDED = "JD_URL_AND_TEXT_BOTH_PROVIDED"
    const val JD_NOT_VALIDATED = "JD_NOT_VALIDATED"
    const val JD_CONTENT_NOT_FOUND = "JD_CONTENT_NOT_FOUND"
    const val INVALID_JD_LENGTH = "INVALID_JD_LENGTH"
    const val INVALID_FREETEXT_LENGTH = "INVALID_FREETEXT_LENGTH"
    const val FREETEXT_NOT_RELEVANT = "FREETEXT_NOT_RELEVANT"
    const val PORTFOLIO_PROCESSING = "PORTFOLIO_PROCESSING"
    const val PORTFOLIO_UPLOAD_FAILED = "PORTFOLIO_UPLOAD_FAILED"

    const val NO_REMAINING_TICKET = "NO_REMAINING_TICKET"
    const val ACCOUNT_SUSPENDED = "ACCOUNT_SUSPENDED"
    const val INTERVIEW_SESSION_NOT_FOUND = "INTERVIEW_SESSION_NOT_FOUND"
    const val AI_TEMPORARILY_UNAVAILABLE = "AI_TEMPORARILY_UNAVAILABLE"
    const val ANSWER_ALREADY_SUBMITTED = "ANSWER_ALREADY_SUBMITTED"
    const val SESSION_ALREADY_ENDED = "SESSION_ALREADY_ENDED"
    const val SESSION_NOT_STARTED = "SESSION_NOT_STARTED"
    const val SESSION_PRELOAD_FAILED = "SESSION_PRELOAD_FAILED"

    const val INVALID_PLATFORM = "INVALID_PLATFORM"
    const val INVALID_VERSION_FORMAT = "INVALID_VERSION_FORMAT"
    const val APP_VERSION_POLICY_NOT_FOUND = "APP_VERSION_POLICY_NOT_FOUND"

    const val NETWORK_UNAVAILABLE = "NETWORK_UNAVAILABLE"
    const val SERVER_ERROR = "SERVER_ERROR"
    const val UNKNOWN = "UNKNOWN"

    /** 다른 API에서 AccessToken 문제로 재발급을 호출해야 하는 코드. */
    fun requiresTokenRefresh(code: String?): Boolean =
        code == TOKEN_EXPIRED || code == INVALID_TOKEN
}

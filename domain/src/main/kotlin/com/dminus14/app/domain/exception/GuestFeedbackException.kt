package com.dminus14.app.domain.exception

/** 호출 전에 Guest Feedback 입력 계약을 만족하지 못한 경우다. */
class GuestFeedbackValidationException(
    message: String,
) : CustomException(
        errCode = ERROR_CODE,
        message = message,
    ) {
    companion object {
        const val MAX_NICKNAME_LENGTH = 12

        /** 별칭 원문의 줄바꿈을 거부하고 양끝 공백을 제거한 1~12자 값을 반환한다. */
        fun normalizeNickname(nickname: String): String {
            val normalized = nickname.trim()
            val validationMessage =
                when {
                    '\n' in nickname || '\r' in nickname -> "별칭에는 줄바꿈을 사용할 수 없습니다."
                    normalized.isEmpty() -> "별칭을 입력해야 합니다."
                    normalized.length > MAX_NICKNAME_LENGTH -> "별칭은 12자 이하여야 합니다."
                    else -> null
                }
            if (validationMessage != null) {
                throw GuestFeedbackValidationException(validationMessage)
            }
            return normalized
        }

        fun isNicknameValid(nickname: String): Boolean =
            try {
                normalizeNickname(nickname)
                true
            } catch (_: GuestFeedbackValidationException) {
                false
            }

        private const val ERROR_CODE = "GUEST_FEEDBACK_VALIDATION_ERROR"
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

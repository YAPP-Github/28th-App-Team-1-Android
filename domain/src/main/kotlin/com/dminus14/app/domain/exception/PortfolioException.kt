package com.dminus14.app.domain.exception

/** PDF가 아닌 파일을 포트폴리오로 올리려 한 경우다 (`INVALID_FILE_TYPE`). */
class InvalidFileTypeException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 포트폴리오 파일이 20MB를 초과한 경우다 (`FILE_TOO_LARGE`). */
class FileTooLargeException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 포트폴리오 PDF가 30페이지를 초과한 경우다 (`PAGE_COUNT_EXCEEDED`). */
class PageCountExceededException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 손상되었거나 파싱할 수 없는 PDF인 경우다 (`INVALID_PDF_FILE`). */
class InvalidPdfFileException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 이미 등록된 포트폴리오가 있어 재업로드가 거부된 경우다 (`PORTFOLIO_ALREADY_EXISTS`). */
class PortfolioAlreadyExistsException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 이번 달 재업로드(교체) 기회를 이미 사용한 경우다 (`REPLACEMENT_LIMIT_EXCEEDED`). */
class ReplacementLimitExceededException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 요청한 포트폴리오를 찾지 못한 경우다 (`PORTFOLIO_NOT_FOUND`). */
class PortfolioNotFoundException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 진행 중인 면접이 사용 중이어서 포트폴리오를 삭제할 수 없는 경우다 (`PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW`). */
class PortfolioDeleteBlockedByInterviewException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 이번 달 삭제 기회를 이미 사용한 경우다 (`DELETE_LIMIT_EXCEEDED`). */
class DeleteLimitExceededException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 포트폴리오가 아직 처리(PROCESSING) 중이어서 세션을 생성할 수 없는 경우다 (`PORTFOLIO_PROCESSING`). */
class PortfolioProcessingException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

/** 포트폴리오 처리가 실패(FAILED_FILE/FAILED_SYSTEM)해 세션을 생성할 수 없는 경우다 (`PORTFOLIO_UPLOAD_FAILED`). */
class PortfolioUploadFailedException(
    errCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(errCode = errCode, message = message, cause = cause)

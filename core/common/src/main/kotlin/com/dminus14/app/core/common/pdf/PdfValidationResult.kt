package com.dminus14.app.core.common.pdf

sealed interface PdfValidationResult {
    data object Valid : PdfValidationResult

    data class Invalid(
        val reason: PdfInvalidReason,
    ) : PdfValidationResult

    data class Error(
        val reason: PdfValidationErrorReason,
    ) : PdfValidationResult
}

enum class PdfInvalidReason {
    INVALID_PAGE_COUNT,
    INVALID_FILE_SIZE,
    UNKNOWN_FILE_SIZE,
    PASSWORD_REQUIRED,
    NOT_SEEKABLE,
    INVALID_PDF_FORMAT,
}

enum class PdfValidationErrorReason {
    FILE_NOT_ACCESSIBLE,
    UNKNOWN_ERROR,
}

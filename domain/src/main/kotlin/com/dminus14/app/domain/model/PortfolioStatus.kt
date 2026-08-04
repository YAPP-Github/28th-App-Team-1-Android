package com.dminus14.app.domain.model

/**
 * 포트폴리오 업로드·처리 상태.
 *
 * 알 수 없는 값은 [UNKNOWN]으로 흡수한다.
 */
enum class PortfolioStatus {
    PROCESSING,
    READY,
    FAILED_FILE,
    FAILED_SYSTEM,
    UNKNOWN,
    ;

    companion object {
        fun fromRaw(rawStatus: String): PortfolioStatus =
            entries.firstOrNull { status -> status.name == rawStatus } ?: UNKNOWN
    }
}

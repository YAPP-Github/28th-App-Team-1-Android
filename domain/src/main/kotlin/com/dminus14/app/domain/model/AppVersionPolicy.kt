package com.dminus14.app.domain.model

/**
 * 서버가 판정한 업데이트 유형.
 *
 * 알 수 없는 값은 [UNKNOWN]으로 흡수한다.
 */
enum class AppVersionUpdateType {
    FORCE,
    OPTIONAL,
    NONE,
    UNKNOWN,
}

/**
 * 앱 버전 정책 조회 결과다.
 *
 * [updateType]이 [AppVersionUpdateType.NONE]이면 [title]과 [body]는 `null`이다.
 */
data class AppVersionPolicy(
    val updateType: AppVersionUpdateType,
    val latestVersion: String,
    val minSupportedVersion: String,
    val storeUrl: String,
    val title: String?,
    val body: String?,
)

package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.AppVersionPolicy

/** 앱 버전 정책(강제/권장 업데이트 여부)을 조회한다. */
interface AppVersionRepository {
    /** [version]의 업데이트 정책을 조회한다. */
    suspend fun checkAppVersion(version: String): AppVersionPolicy
}

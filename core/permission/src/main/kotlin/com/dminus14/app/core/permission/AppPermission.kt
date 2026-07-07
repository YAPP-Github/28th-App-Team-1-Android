package com.dminus14.app.core.permission

import android.Manifest

/**
 * 앱에서 사용하는 런타임 권한.
 *
 * Manifest 선언은 [app] 모듈에서 일원화한다.
 */
enum class AppPermission(
    val manifestName: String,
) {
    CAMERA(Manifest.permission.CAMERA),
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
}

package com.dminus14.app.convention

import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Compose Multiplatform Convention Plugin.
 *
 * Plugin ID: `dminus14.compose.multiplatform`
 * 적용 대상: Compose Multiplatform 및 Kotlin Multiplatform이 필요한 모듈 (예: `:designsystem`)
 *
 * KMP와 CMP에 필요한 플러그인과 라이브러리 의존성을 추가한다.
 */
class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("kotlin-compose"))
            pluginManager.apply(pluginId("kotlin-multiplatform"))
            pluginManager.apply(pluginId("compose-multiplatform"))
        }
}

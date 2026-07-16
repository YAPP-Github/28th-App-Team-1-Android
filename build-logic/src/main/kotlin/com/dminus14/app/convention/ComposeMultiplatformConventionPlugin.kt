package com.dminus14.app.convention

import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Compose Multiplatform 모듈의 최소 plugin 기반을 구성한다.
 *
 * Plugin ID: `dminus14.compose.multiplatform`
 *
 * Kotlin Multiplatform, Compose compiler와 Compose Multiplatform Gradle plugin만 적용한다.
 * target, UI 의존성, Preview와 resource 정책은 상위 composite/capability plugin이 소유한다.
 */
class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("kotlin-compose"))
            pluginManager.apply(pluginId("kotlin-multiplatform"))
            pluginManager.apply(pluginId("compose-multiplatform"))
        }
}

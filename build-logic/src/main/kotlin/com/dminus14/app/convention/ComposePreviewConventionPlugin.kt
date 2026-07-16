package com.dminus14.app.convention

import com.dminus14.app.extension.addAndroidComposePreviewDependencies
import com.dminus14.app.extension.addComposeMultiplatformPreviewDependencies
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * 일반 Android와 KMP Android Library 모듈에 Compose Preview 기능을 제공한다.
 *
 * Plugin ID: `dminus14.compose.preview`
 *
 * 적용 환경을 감지해 Android 또는 `commonMain`에 맞는 annotation/tooling 의존성을 구성한다.
 * Compose 기반 plugin과 Android target 자체는 적용하지 않는다.
 */
class ComposePreviewConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            var configured = false

            pluginManager.withPlugin(pluginId("android-application")) {
                configured = true
                addAndroidComposePreviewDependencies()
            }
            pluginManager.withPlugin(pluginId("android-library")) {
                configured = true
                addAndroidComposePreviewDependencies()
            }
            pluginManager.withPlugin(pluginId("android-kmp-library")) {
                configured = true
                val kotlinMultiplatformExtension =
                    extensions.getByType(KotlinMultiplatformExtension::class.java)
                addComposeMultiplatformPreviewDependencies(kotlinMultiplatformExtension)
            }

            afterEvaluate {
                check(configured) {
                    "dminus14.compose.preview는 Android 또는 KMP Android Library 모듈에만 적용할 수 있습니다."
                }
            }
        }
}

package com.dminus14.app.convention.capability

import com.dminus14.app.extension.addAndroidComposeResourcesDependency
import com.dminus14.app.extension.addComposeMultiplatformResourcesDependency
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * 허용된 UI 소비자에 공용 Compose Multiplatform 리소스 모듈을 연결한다.
 *
 * Plugin ID: `dminus14.compose.resources`
 *
 * `:app`, `:feature:*:impl`, `:designsystem`에서만 사용한다. 일반 Android와 KMP 환경을 감지해
 * 올바른 dependency configuration에 `:core:resources`를 추가한다.
 */
class ComposeResourcesConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            check(path != ":core:resources" && path != ":catalog") {
                "dminus14.compose.resources는 $path 모듈에 적용할 수 없습니다."
            }

            var configured = false

            pluginManager.withPlugin(pluginId("android-application")) {
                configured = true
                addAndroidComposeResourcesDependency()
            }
            pluginManager.withPlugin(pluginId("android-library")) {
                configured = true
                addAndroidComposeResourcesDependency()
            }
            pluginManager.withPlugin(pluginId("android-kmp-library")) {
                configured = true
                val kotlinMultiplatformExtension =
                    extensions.getByType(KotlinMultiplatformExtension::class.java)
                addComposeMultiplatformResourcesDependency(kotlinMultiplatformExtension)
            }

            afterEvaluate {
                check(configured) {
                    "dminus14.compose.resources는 Android 또는 KMP Android Library 모듈에만 적용할 수 있습니다."
                }
            }
        }
}

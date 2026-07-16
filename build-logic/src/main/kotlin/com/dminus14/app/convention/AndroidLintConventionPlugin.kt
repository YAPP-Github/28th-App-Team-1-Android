package com.dminus14.app.convention

import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Android Application/Library가 Android Lint를 사용할 수 있는 모듈인지 검증한다.
 *
 * Plugin ID: `dminus14.android.lint`
 *
 * AGP가 제공하는 Lint task를 품질 bundle에서 일관되게 참조하기 위한 capability marker다.
 * Spotless, Detekt, Compose lint check와 모듈별 lint 예외는 소유하지 않는다.
 */
class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            afterEvaluate {
                check(
                    pluginManager.hasPlugin(pluginId("android-application")) ||
                        pluginManager.hasPlugin(pluginId("android-library")),
                ) {
                    "dminus14.android.lint는 Android Application 또는 Library 모듈에만 적용할 수 있습니다."
                }
            }
        }
}

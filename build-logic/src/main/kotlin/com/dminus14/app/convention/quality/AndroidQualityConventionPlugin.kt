package com.dminus14.app.convention.quality

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Android 모듈의 저장소 표준 품질 plugin과 검증 task 흐름을 조합한다.
 *
 * Plugin ID: `dminus14.android.quality`
 *
 * Kotlin Quality와 Android Lint를 적용하고 `lintDebug`가 Spotless와 Detekt 검증 뒤 실행되게
 * 연결한다. 각 도구의 상세 설정은 직접 소유하지 않는다.
 */
class AndroidQualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("dminus14.kotlin.quality")
            pluginManager.apply("dminus14.android.lint")

            tasks.matching { it.name == "lintDebug" }.configureEach {
                dependsOn("spotlessCheck", "detekt")
            }
        }
}

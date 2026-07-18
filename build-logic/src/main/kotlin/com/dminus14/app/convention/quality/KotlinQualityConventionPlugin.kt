package com.dminus14.app.convention.quality

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Kotlin 모듈의 저장소 표준 포맷과 정적 분석 plugin을 조합한다.
 *
 * Plugin ID: `dminus14.kotlin.quality`
 *
 * Spotless와 Detekt를 적용만 하며 각 도구의 상세 설정은 소유하지 않는다. JVM/KMP 모듈과
 * Android Quality bundle의 기반으로 사용한다.
 */
class KotlinQualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("dminus14.spotless")
            pluginManager.apply("dminus14.detekt")
        }
}

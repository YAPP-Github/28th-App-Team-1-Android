package com.dminus14.app.convention.quality

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Kotlin 소스와 Gradle Kotlin DSL에 저장소 공통 Spotless 포맷 규칙을 적용한다.
 *
 * Plugin ID: `dminus14.spotless`
 *
 * 정적 분석과 Android Lint는 구성하지 않으며 [KotlinQualityConventionPlugin]이 이 플러그인을
 * 표준 품질 bundle로 조합한다.
 */
class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("com.diffplug.spotless")

            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("**/*.kt")
                    targetExclude("**/build/**")
                    ktlint()
                }
                kotlinGradle {
                    target("**/*.gradle.kts")
                    targetExclude("**/build/**")
                    ktlint()
                }
            }
        }
}

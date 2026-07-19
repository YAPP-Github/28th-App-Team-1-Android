package com.dminus14.app.convention.quality

import com.dminus14.app.extension.BuildConfig
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

/**
 * Kotlin 소스에 저장소 공통 Detekt 정적 분석과 report 정책을 적용한다.
 *
 * Plugin ID: `dminus14.detekt`
 *
 * `BuildConfig`의 JVM target을 사용하며 포맷과 Android Lint는 구성하지 않는다.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                config.setFrom(rootProject.layout.projectDirectory.file("config/detekt.yml"))
            }

            tasks.withType<Detekt>().configureEach {
                jvmTarget = BuildConfig.JVM_VERSION.toString()

                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    sarif.required.set(false)
                    md.required.set(false)
                }
            }
        }
}

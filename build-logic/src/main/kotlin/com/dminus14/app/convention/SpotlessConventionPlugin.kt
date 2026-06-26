package com.dminus14.app.convention

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Applies the shared Spotless formatting setup for Kotlin sources and Gradle scripts.
 */
class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.diffplug.spotless")

        extensions.configure<SpotlessExtension> {
            // Format Kotlin source files in the current module.
            kotlin {
                target("**/*.kt")
                targetExclude("**/build/**")
                ktlint()
            }
            // Format Kotlin Gradle scripts with ktlint and the repository .editorconfig.
            kotlinGradle {
                target("**/*.gradle.kts")
                targetExclude("**/build/**")
                ktlint()
            }
        }
    }
}

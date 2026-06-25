package com.dminus14.app.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies the shared linting setup for non-Android Kotlin modules.
 */
class KotlinLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // Pure Kotlin modules only need formatting and Detekt analysis.
        pluginManager.apply("dminus14.spotless")
        pluginManager.apply("dminus14.detekt")
    }
}

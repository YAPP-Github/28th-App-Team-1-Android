package com.dminus14.app.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Applies the shared linting setup for Android application and library modules.
 */
class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // Reuse the base formatting and static analysis conventions.
        pluginManager.apply("dminus14.spotless")
        pluginManager.apply("dminus14.detekt")

        // Android lint configuration is only available after an Android plugin is applied.
        pluginManager.withPlugin("com.android.application") {
            configureAndroidLint()
        }
        pluginManager.withPlugin("com.android.library") {
            configureAndroidLint()
        }
    }

    private fun Project.configureAndroidLint() {
        // Register Compose-specific lint checks from the shared version catalog.
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies.add("lintChecks", libs.findLibrary("slack.compose.lint.checks").get())

        // Keep lintDebug as the single verification entry point for Android modules.
        tasks.matching { it.name == "lintDebug" }.configureEach {
            dependsOn("spotlessCheck", "detekt")
        }
    }
}

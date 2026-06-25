package com.dminus14.app.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

/**
 * Applies Detekt with the repository-wide configuration and reporting defaults.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("io.gitlab.arturbosch.detekt")

        // Use the root Detekt configuration so every module follows the same rule set.
        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            config.setFrom(rootProject.layout.projectDirectory.file("config/detekt.yml"))
        }

        tasks.withType<Detekt>().configureEach {
            // Align Detekt type resolution with the JVM target used by application code.
            jvmTarget = "11"

            // Generate machine-readable reports and a browsable HTML report for review.
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

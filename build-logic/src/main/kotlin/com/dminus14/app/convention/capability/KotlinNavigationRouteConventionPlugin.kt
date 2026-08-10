package com.dminus14.app.convention.capability

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Navigation 3 route 계약에 필요한 Kotlin Serialization과 runtime dependency를 제공한다.
 *
 * Plugin ID: `dminus14.kotlin.navigation-route`
 */
class KotlinNavigationRouteConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("kotlin-serialization"))

            dependencies {
                add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
            }
        }
    }
}

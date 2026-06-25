/**
 * Android Feature 모듈 Convention Plugin.
 *
 * Plugin ID: `dminus14.android.feature`
 * 적용 대상: `feature-*` UI feature 모듈
 *
 * Library + Compose + Hilt plugin을 적용하고, Navigation3/Hilt Navigation 등 feature 공통 의존성을 추가한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.pluginId
import com.dminus14.app.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("dminus14-android-library"))
            pluginManager.apply(pluginId("dminus14-android-compose"))
            pluginManager.apply(pluginId("dminus14-android-hilt"))

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
                add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
            }
        }
    }
}

/**
 * Android Application 모듈 Convention Plugin.
 *
 * Plugin ID: `dminus14.android.application`
 * 적용 대상: `:app`
 *
 * Application + Compose + Hilt plugin을 적용하고, SDK/앱 설정 및 app 공통 의존성(Navigation3 포함)을 구성한다.
 */
package com.dminus14.app.convention

import com.android.build.api.dsl.ApplicationExtension
import com.dminus14.app.extension.pluginId
import com.dminus14.app.extension.configureAndroidApplication
import com.dminus14.app.extension.configureKotlinAndroid
import com.dminus14.app.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("android-application"))
            pluginManager.apply(pluginId("dminus14-android-compose"))
            pluginManager.apply(pluginId("dminus14-android-hilt"))

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidApplication(this)
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-activity-compose").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
                add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
            }
        }
    }
}

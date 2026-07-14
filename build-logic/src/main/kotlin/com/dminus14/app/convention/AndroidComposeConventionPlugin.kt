/**
 * Jetpack Compose Convention Plugin.
 *
 * Plugin ID: `dminus14.android.compose`
 * 적용 대상: Compose UI가 필요한 Application/Library 모듈
 *
 * kotlin.compose plugin을 적용하고, Compose buildFeatures 및 BOM/UI 의존성을 구성한다.
 */
package com.dminus14.app.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.dminus14.app.extension.pluginId
import com.dminus14.app.extension.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("kotlin-compose"))

            pluginManager.withPlugin(pluginId("android-application")) {
                extensions.configure<ApplicationExtension> {
                    configureAndroidCompose(this)
                }
            }

            pluginManager.withPlugin(pluginId("android-library")) {
                extensions.configure<LibraryExtension> {
                    configureAndroidCompose(this)
                }
            }
        }
    }
}

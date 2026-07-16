package com.dminus14.app.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.dminus14.app.extension.configureAndroidCompose
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * 일반 Android 모듈에 Jetpack Compose 제품 UI 환경을 제공한다.
 *
 * Plugin ID: `dminus14.android.compose`
 *
 * Compose compiler plugin, build feature와 공통 제품 UI 의존성만 구성한다. Preview, 공용
 * 리소스, 테스트, lint와 Activity Compose는 별도 책임이므로 포함하지 않는다.
 */
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

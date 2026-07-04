/**
 * Android Library 모듈 Convention Plugin.
 *
 * Plugin ID: `dminus14.android.library`
 * 적용 대상: `core`, `data` 등 Android Library 모듈
 *
 * com.android.library plugin을 적용하고 SDK/JVM 공통 설정을 구성한다.
 * AGP 9.0+ built-in Kotlin을 사용하므로 kotlin-android plugin은 적용하지 않는다.
 */
package com.dminus14.app.convention

import com.android.build.api.dsl.LibraryExtension
import com.dminus14.app.extension.pluginId
import com.dminus14.app.extension.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("android-library"))

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }
        }
    }
}

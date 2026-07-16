package com.dminus14.app.convention

import com.android.build.api.dsl.LibraryExtension
import com.dminus14.app.extension.configureKotlinAndroid
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Android Library의 기반 설정을 구성하는 Convention Plugin이다.
 *
 * Plugin ID: `dminus14.android.library`
 *
 * Android Library plugin과 공통 SDK/JVM target만 구성한다. AGP 9의 built-in Kotlin을 사용하며
 * namespace와 Compose, Hilt, 테스트 같은 기능은 적용 모듈이 별도로 선택한다.
 */
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

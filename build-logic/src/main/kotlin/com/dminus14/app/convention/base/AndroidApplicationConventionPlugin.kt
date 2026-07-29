package com.dminus14.app.convention.base

import com.android.build.api.dsl.ApplicationExtension
import com.dminus14.app.extension.configureAndroidApplication
import com.dminus14.app.extension.configureAndroidSigning
import com.dminus14.app.extension.configureKotlinAndroid
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * `:app`의 Android Application 기반 설정을 구성하는 Convention Plugin이다.
 *
 * Plugin ID: `dminus14.android.application`
 *
 * Android Application plugin, 공통 SDK/JVM target과 앱 식별자·버전·build type·signing만 구성한다.
 * Compose, Hilt, Navigation, 테스트와 품질 기능은 각각의 capability plugin을 별도로 적용한다.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("android-application"))

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidApplication(this)
                configureAndroidSigning(this)
            }
        }
    }
}

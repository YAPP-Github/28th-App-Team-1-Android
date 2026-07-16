package com.dminus14.app.convention

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * DI가 필요한 Android 모듈에 Hilt와 KSP 구성을 제공한다.
 *
 * Plugin ID: `dminus14.android.hilt`
 *
 * Hilt Android plugin, KSP, Hilt runtime과 compiler만 구성한다. Navigation과 모듈별 Hilt
 * binding은 소유하지 않는다.
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("kotlin-ksp"))
            pluginManager.apply(pluginId("dagger-hilt-android"))

            dependencies {
                add("ksp", libs.findLibrary("dagger-hilt-compiler").get())
                add("implementation", libs.findLibrary("dagger-hilt-android").get())
            }
        }
    }
}

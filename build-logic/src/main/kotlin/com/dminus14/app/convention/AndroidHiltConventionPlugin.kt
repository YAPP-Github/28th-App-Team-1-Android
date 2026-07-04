/**
 * Hilt Convention Plugin.
 *
 * Plugin ID: `dminus14.android.hilt`
 * 적용 대상: DI가 필요한 Android 모듈
 *
 * KSP + Hilt Android plugin을 적용하고, compiler/runtime 의존성을 추가한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

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

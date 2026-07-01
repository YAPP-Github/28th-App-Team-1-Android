/**
 * Room Convention Plugin.
 *
 * Plugin ID: `dminus14.android.room`
 * 적용 대상: 로컬 DB가 필요한 모듈 (예: `:data`)
 *
 * KSP plugin을 적용하고 Room runtime/ktx/compiler 의존성을 추가한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("kotlin-ksp"))

            dependencies {
                add("implementation", libs.findLibrary("androidx-room-runtime").get())
                add("implementation", libs.findLibrary("androidx-room-ktx").get())
                add("ksp", libs.findLibrary("androidx-room-compiler").get())
            }
        }
    }
}

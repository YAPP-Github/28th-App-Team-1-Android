package com.dminus14.app.convention.capability

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 로컬 데이터베이스가 필요한 Android 모듈에 Room과 KSP 구성을 제공한다.
 *
 * Plugin ID: `dminus14.android.room`
 *
 * Room runtime/KTX/compiler와 KSP만 구성하며 database schema와 구현 코드는 소유하지 않는다.
 */
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

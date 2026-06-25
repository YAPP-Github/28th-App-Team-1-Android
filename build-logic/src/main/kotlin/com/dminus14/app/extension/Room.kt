/**
 * Room 의존성 헬퍼.
 *
 * room-runtime, room-ktx, room-compiler(KSP) 의존성을 추가한다.
 */
package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureRoom() {
    dependencies {
        add("implementation", libs.findLibrary("androidx-room-runtime").get())
        add("implementation", libs.findLibrary("androidx-room-ktx").get())
        add("ksp", libs.findLibrary("androidx-room-compiler").get())
    }
}

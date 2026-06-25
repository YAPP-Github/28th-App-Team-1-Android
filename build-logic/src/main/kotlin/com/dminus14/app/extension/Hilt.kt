/**
 * Hilt 의존성 헬퍼.
 *
 * KSP compiler와 hilt-android runtime 의존성을 추가한다.
 * Hilt plugin/KSP plugin 적용은 [AndroidHiltConventionPlugin]에서 처리한다.
 */
package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureHilt() {
    dependencies {
        add("ksp", libs.findLibrary("dagger-hilt-compiler").get())
        add("implementation", libs.findLibrary("dagger-hilt-android").get())
    }
}

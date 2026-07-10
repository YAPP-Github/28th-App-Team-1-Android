/**
 * Jetpack Compose 빌드 설정 및 의존성 헬퍼.
 *
 * buildFeatures.compose 활성화와 Compose BOM, UI/Material3, Activity Compose, 테스트/디버그 의존성을 일괄 추가한다.
 */
package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Application 모듈 Compose 설정 */
internal fun Project.configureAndroidCompose(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}

/** Library 모듈 Compose 설정 */
internal fun Project.configureAndroidCompose(
    libraryExtension: LibraryExtension,
) {
    libraryExtension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}

private fun Project.addComposeDependencies() {
    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("androidx-compose-ui").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-compose-material3").get())
        add("implementation", libs.findLibrary("androidx-activity-compose").get())
        add("testImplementation", libs.findLibrary("junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
        add("androidTestImplementation", platform(bom))
        add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
    }
}

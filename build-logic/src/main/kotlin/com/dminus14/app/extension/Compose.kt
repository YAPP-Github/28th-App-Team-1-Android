/**
 * Jetpack Compose 빌드 설정 및 의존성 헬퍼.
 *
 * buildFeatures.compose 활성화와 Compose BOM, UI/Material3, 테스트/디버그 의존성을 일괄 추가한다.
 */
package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.dminus14.app.extension.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/** Application 모듈 Compose 설정 */
internal fun Project.configureAndroidCompose(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}
/**
 * Compose Multiplatform을 사용하는 모든 모듈에 필요한 의존성을 추가합니다.
 */
internal fun KotlinDependencyHandler.addComposeMultiplatformLibraries(project: Project) {
    val libs = project.libs

    implementation(libs.findLibrary("compose-runtime").get())
    implementation(libs.findLibrary("compose-foundation").get())
    implementation(libs.findLibrary("compose-material3").get())
    implementation(libs.findLibrary("compose-ui").get())
    implementation(libs.findLibrary("compose-components-resources").get())
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
        add("testImplementation", libs.findLibrary("junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
        add("androidTestImplementation", platform(bom))
        add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
    }
}

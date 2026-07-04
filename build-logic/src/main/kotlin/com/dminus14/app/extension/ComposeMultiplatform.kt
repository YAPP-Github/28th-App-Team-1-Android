package com.dminus14.app.extension

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

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

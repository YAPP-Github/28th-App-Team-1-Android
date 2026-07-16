package com.dminus14.app.extension

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/**
 * Compose Multiplatform UI를 `commonMain` 또는 platform source set에서 구현할 때 필요한
 * Runtime, Foundation, UI, Material3와 Compose Resources 라이브러리를 추가한다.
 *
 * [ComposeMultiplatformUiLibraryConventionPlugin]과
 * [ComposeMultiplatformWasmApplicationConventionPlugin]이 호출한다. project dependency,
 * target, Preview와 resource 공개 정책은 구성하지 않는다.
 */
internal fun KotlinDependencyHandler.addComposeMultiplatformUiDependencies(project: Project) {
    val libs = project.libs

    implementation(libs.findLibrary("compose-runtime").get())
    implementation(libs.findLibrary("compose-foundation").get())
    implementation(libs.findLibrary("compose-material3").get())
    implementation(libs.findLibrary("compose-ui").get())
    implementation(libs.findLibrary("compose-components-resources").get())
}

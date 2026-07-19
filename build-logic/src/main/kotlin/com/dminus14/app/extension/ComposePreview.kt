package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * 일반 Android 모듈에 Preview annotation과 debug tooling 의존성을 추가한다.
 *
 * [ComposePreviewConventionPlugin]이 Android Application 또는 Library plugin 적용을 확인한 뒤
 * 호출한다. Compose build feature와 제품 UI 의존성은 구성하지 않는다.
 */
internal fun Project.addAndroidComposePreviewDependencies() {
    dependencies {
        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
    }
}

/**
 * KMP `commonMain`에 Preview annotation을, Android runtime classpath에 Preview tooling을 추가한다.
 *
 * [ComposePreviewConventionPlugin]이 Android KMP Library target을 확인한 뒤 호출한다.
 * Android target과 Compose Multiplatform plugin 자체는 구성하지 않는다.
 */
internal fun Project.addComposeMultiplatformPreviewDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.sourceSets.named("commonMain") {
        dependencies {
            implementation(project.libs.findLibrary("compose-ui-tooling-preview").get())
        }
    }
    dependencies.add(
        "androidRuntimeClasspath",
        libs.findLibrary("compose-ui-tooling").get(),
    )
}

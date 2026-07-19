package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * 일반 Android 모듈의 제품 classpath에 `:core:resources`를 추가한다.
 *
 * [ComposeResourcesConventionPlugin]이 Android Application 또는 Library plugin 적용을 확인한 뒤
 * 호출한다. Compose Resources 공개 정책은 `:core:resources`가 소유한다.
 */
internal fun Project.addAndroidComposeResourcesDependency() {
    dependencies {
        add("implementation", project(":core:resources"))
    }
}

/**
 * KMP `commonMain`에 `:core:resources` project dependency를 추가한다.
 *
 * [ComposeResourcesConventionPlugin]이 Android KMP Library target을 확인한 뒤 호출한다.
 * Catalog와 `:core:resources` 자체에는 적용하지 않는다.
 */
internal fun Project.addComposeMultiplatformResourcesDependency(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.sourceSets.named("commonMain") {
        dependencies {
            implementation(project(":core:resources"))
        }
    }
}

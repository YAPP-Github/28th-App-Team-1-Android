package com.dminus14.app.extension

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
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

/** Kotlin Multiplatform Android Library target 설정. */
internal fun Project.configureKotlinMultiplatformAndroidLibrary(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.targets
        .withType<KotlinMultiplatformAndroidLibraryTarget>()
        .configureEach {
            compileSdk {
                version = release(AndroidSdkVersions.COMPILE)
            }

            minSdk = AndroidSdkVersions.MIN

            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
}

/** Kotlin Multiplatform Wasm browser Library target 설정. */
@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatformWasmBrowserLibrary(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.apply {
        wasmJs {
            browser()
        }
    }
}

/** Kotlin Multiplatform에서 타겟하는 Wasm 환경 설정 */
@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatformWasm(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.apply {
        wasmJs {
            browser()
            binaries.executable()
        }
    }

    extensions.configure<ComposeExtension>("compose") {
        extensions.configure<ResourcesExtension>("resources") {
            publicResClass = false
            packageOfResClass = "com.dminus14.catalog.generated.resources"
            generateResClass = ResourcesExtension.ResourceClassGeneration.Always
        }
    }
}

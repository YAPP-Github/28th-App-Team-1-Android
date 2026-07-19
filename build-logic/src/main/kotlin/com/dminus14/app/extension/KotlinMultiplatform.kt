package com.dminus14.app.extension

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP Android Library target의 SDK와 Kotlin JVM target을 구성한다.
 *
 * [ComposeMultiplatformLibraryConventionPlugin]이 Android KMP Library plugin 적용 후 호출한다.
 * namespace, UI 의존성, Preview와 resource 공개 정책은 구성하지 않는다.
 */
internal fun Project.configureKotlinMultiplatformAndroidLibrary(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.targets
        .withType<KotlinMultiplatformAndroidLibraryTarget>()
        .configureEach {
            compileSdk {
                version = release(BuildConfig.ANDROID_COMPILE_SDK)
            }

            minSdk = BuildConfig.ANDROID_MIN_SDK

            compilerOptions {
                jvmTarget.set(BuildConfig.KOTLIN_JVM_TARGET)
            }
        }
}

/**
 * 배포 library가 사용할 Kotlin/Wasm browser target을 구성한다.
 *
 * [ComposeMultiplatformLibraryConventionPlugin]이 호출하며 executable binary는 생성하지 않는다.
 */
@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatformWasmBrowserLibrary(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.wasmJs {
        browser()
    }
}

/**
 * Web 애플리케이션이 사용할 실행 가능한 Kotlin/Wasm browser target을 구성한다.
 *
 * [ComposeMultiplatformWasmApplicationConventionPlugin]이 호출하며 UI와 project dependency는
 * 구성하지 않는다.
 */
@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatformWasmBrowserApplication(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.wasmJs {
        browser()
        binaries.executable()
    }
}

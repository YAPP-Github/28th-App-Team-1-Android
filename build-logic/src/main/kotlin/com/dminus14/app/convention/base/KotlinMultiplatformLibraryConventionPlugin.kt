package com.dminus14.app.convention.base

import com.dminus14.app.extension.configureKotlinMultiplatformJvmAndWasmLibrary
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Compose와 Android에 의존하지 않는 Kotlin Multiplatform Library 기반을 구성한다.
 *
 * Plugin ID: `dminus14.kotlin.multiplatform.library`
 *
 * Kotlin Multiplatform plugin과 JVM/Wasm browser library target만 구성한다. UI dependency,
 * Android target과 executable binary는 적용 대상 모듈의 책임이 아니다.
 */
class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("kotlin-multiplatform"))

            pluginManager.withPlugin(pluginId("kotlin-multiplatform")) {
                extensions.configure<KotlinMultiplatformExtension> {
                    configureKotlinMultiplatformJvmAndWasmLibrary(this)
                }
            }
        }
}

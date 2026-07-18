package com.dminus14.app.convention.base

import com.dminus14.app.extension.configureKotlinMultiplatformAndroidLibrary
import com.dminus14.app.extension.configureKotlinMultiplatformWasmBrowserLibrary
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Android와 Wasm browser 산출물을 제공하는 CMP Library target을 구성한다.
 *
 * Plugin ID: `dminus14.compose.multiplatform.library`
 *
 * `:core:resources`와 CMP UI Library bundle의 기반으로 사용한다. UI·project dependency,
 * namespace, Preview와 Compose Resources 공개 정책은 적용 대상 또는 다른 plugin이 소유한다.
 */
class ComposeMultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform"))
            pluginManager.apply(pluginId("android-kmp-library"))

            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                    configureKotlinMultiplatformAndroidLibrary(this)
                    configureKotlinMultiplatformWasmBrowserLibrary(this)
                }
            }
        }
}

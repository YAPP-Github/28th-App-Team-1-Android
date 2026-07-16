package com.dminus14.app.convention

import com.dminus14.app.extension.configureKotlinMultiplatformAndroidLibrary
import com.dminus14.app.extension.configureKotlinMultiplatformWasmBrowserLibrary
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Android와 Wasm browser를 지원하는 Compose Multiplatform Library Convention Plugin.
 *
 * UI 의존성, 프로젝트 의존성, namespace, Compose Resources 공개 정책은 적용 대상 모듈이 선언한다.
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

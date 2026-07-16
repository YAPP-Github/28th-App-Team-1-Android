package com.dminus14.app.convention

import com.dminus14.app.extension.addComposeMultiplatformUiDependencies
import com.dminus14.app.extension.configureKotlinMultiplatformWasmBrowserApplication
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Compose Multiplatform 기반 Wasm browser 애플리케이션 환경을 구성한다.
 *
 * Plugin ID: `dminus14.compose.multiplatform.wasm-application`
 *
 * 실행 가능한 Wasm target과 `wasmJsMain` UI 라이브러리만 구성한다. `:catalog`가 사용하며
 * `:designsystem` 의존성, catalog resource package와 Preview는 모듈 빌드 파일이 소유한다.
 */
class ComposeMultiplatformWasmApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform"))

            pluginManager.withPlugin(pluginId("kotlin-multiplatform")) {
                val kotlinMultiplatformExtension =
                    extensions.getByType(KotlinMultiplatformExtension::class.java)
                configureKotlinMultiplatformWasmBrowserApplication(kotlinMultiplatformExtension)
                kotlinMultiplatformExtension.sourceSets.named("wasmJsMain") {
                    dependencies {
                        addComposeMultiplatformUiDependencies(project)
                    }
                }
            }
        }
}
